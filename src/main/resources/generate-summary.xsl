<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:x="http://www.jenitennison.com/xslt/xspec"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    exclude-result-prefixes="xs math"
    version="3.0">
    <xsl:param name="tempdir" select="resolve-uri('xspec-temp-files/')"/>
    <xsl:template match="/">
        <xsl:variable name="collection" select="collection($tempdir || '?select=*-result.xml;recurse=yes;on-error=ignore')"/>
        <xsl:variable name="collection" select="collection($tempdir || '?select=xspec.execution.properties.xml;recurse=yes;on-error=ignore')"/>
        <xsl:variable name="single-reports" as="element()*">
            <xsl:for-each select="$collection">
                <xsl:variable name="result" select="resolve-uri('test-result.xml', base-uri(.))"/>
                <xsl:variable name="name-info" select="x:get-name-info(.)"/>
                <xsl:choose>
                    <xsl:when test="doc-available($result)">
                        <xsl:variable name="report" select="doc($result)/x:report"/>
                        <xsl:variable name="name" select="$report/@xspec/tokenize(., '/')[last()]"/>
                        <xsl:variable name="tests" select="$report//x:scenario/x:test"/>
                        <xsl:variable name="passed" select="count($tests[@successful = 'true'])"/>
                        <xsl:variable name="failed" select="count($tests[@successful = 'false'])"/>
                        <xsl:variable name="pending" select="count($tests[@pending])"/>
                        <xsl:variable name="total" select="sum(($passed, $failed, $pending))"/>
                        <report xspec="{$report/@xspec}" 
                            name="{$name-info?name}"
                            href="{$name-info?href}"
                            passed="{$passed}"
                            failed="{$failed}"
                            pending="{$pending}"
                            errors="0"
                            total="{$total}"
                            />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="xspec-url" select="key('property-name', 'xspec.file.url')/@value"/>
                        <xsl:variable name="xspec" select="doc($xspec-url)"/>
                        <xsl:variable name="scenarios" select="$xspec//x:scenario"/>
                        <xsl:variable name="tests" select="
                            $scenarios/(x:expect | x:*[local-name() => starts-with('expect-')])
                            "/>
                        
                        <xsl:variable name="pending-scenarios" select="$xspec//x:scenario[@pending]//*"/>
                        <xsl:variable name="pending-scenarios" select="$xspec//x:pending//x:scenario"/>
                        <xsl:variable name="pending-tests" select="
                            $pending-scenarios/(x:expect | x:*[local-name() => starts-with('expect-')])
                            | $tests[@pending]
                            "/>
                        
                        <xsl:variable name="tests" select="$tests except $pending-tests"/>
                        
                        <xsl:variable name="test-count" select="count($tests)"/>
                        <xsl:variable name="pending-count" select="count($pending-tests)"/>
                        <report xspec="{$xspec-url}" 
                            name="{$name-info?name}"
                            href="{$name-info?href}"
                            passed="0"
                            failed="0"
                            pending="{$pending-count}"
                            errors="{$test-count}"
                            total="{$test-count + $pending-count}"
                        />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:variable name="single-reports" as="element()*" select="$single-reports"/>
        
        
        <xsl:variable name="sum_failures" select="sum($single-reports/@failed)"/>
        <xsl:variable name="sum_passed" select="sum($single-reports/@passed)"/>
        <xsl:variable name="sum_pending" select="sum($single-reports/@pending)"/>
        <xsl:variable name="sum_errors" select="sum($single-reports/@errors)"/>
        <xsl:variable name="sum_total" select="sum($single-reports/@total)"/>
        
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <title xsl:expand-text="yes"
                    >Summary Report of XSpec Maven plugin (passed: {$sum_passed} / pending: {$sum_pending} / failed: {$sum_failures} / errors: {$sum_errors} / total: {$sum_total})</title>
                <style type="text/css">
                    .emphasis {
                        font-weight: bold !important;
                    }
                    <xsl:value-of select="unparsed-text('xspec-report-theme-classic.css')"/>
                </style>
            </head>
            <body id="testReport">
                <h1>Summary XSpec Report</h1>
                
                
                <table class="xspec">
                    <colgroup>
                        <col style="width:70%" />
                        <col style="width:6%" />
                        <col style="width:6%" />
                        <col style="width:6%" />
                        <col style="width:6%" />
                        <col style="width:6%" />
                    </colgroup>
                    <thead>
                        <tr xsl:expand-text="yes">
                            <th></th>
                            <th class="totals">passed: {$sum_passed}</th>
                            <th class="totals">pending: {$sum_pending}</th>
                            <th class="totals {'emphasis'[$sum_failures gt 0]}">failed: {$sum_failures}</th>
                            <th class="totals {'emphasis'[$sum_errors gt 0]}">errors: {$sum_errors}</th>
                            <th class="totals">total: {$sum_total}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:for-each select="$single-reports" expand-text="yes">
                            <tr class="{
                                if (@failed > 0) 
                                then 'failed' 
                                else if (@errors > 0) 
                                then 'errors' 
                                else if (@passed = 0) 
                                then 'pending' 
                                else 'successful'
                                }">
                                <th>
                                    <a href="{@href}">
                                        <xsl:value-of select="@name"/>
                                    </a>
                                </th>
                                <th class="totals">{@passed}</th>
                                <th class="totals">{@pending}</th>
                                <xsl:variable name="failed" select="@failed"/>
                                <th class="totals {'emphasis'[$failed > 0]}">{$failed}</th>
                                <xsl:variable name="errors" select="@errors"/>
                                <th class="totals {'emphasis'[$errors > 0]}">{$errors}</th>
                                <th class="totals">{@total}</th>
                            </tr>
                        </xsl:for-each>
                    </tbody>
                </table>
                
                
            </body>
        </html>
    </xsl:template>
    
    <xsl:key name="property-name" match="property" use="@name"/>
    
    <xsl:function name="x:get-name-info" as="map(xs:string, item())">
        <xsl:param name="properties" as="document-node()"/>
        <xsl:variable name="xspec.file.url" select="$properties/key('property-name', 'xspec.file.url')/@value"/>
        <xsl:variable name="name" select="$xspec.file.url/tokenize(., '/')[last()]"/>
        
        <xsl:variable name="report.file" select="$properties/key('property-name', 'report.file')/@value"/>
        <xsl:variable name="report.dir.url" select="$properties/key('property-name', 'report.dir.url')/@value"/>
        <xsl:variable name="report.file.url" select="$properties/key('property-name', 'report.file.url')/@value"/>
        <xsl:variable name="name" select="$properties/key('property-name', 'xspec.file.basename')/@value"/>
        <xsl:variable name="href" select="substring-after($report.file.url, $report.dir.url)"/>
        <xsl:sequence select="map{
                'name' : $name,
                'href' : $href,
                'label' : $name
            }"/>
        
    </xsl:function>
</xsl:stylesheet>