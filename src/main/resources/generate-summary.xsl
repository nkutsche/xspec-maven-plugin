<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:x="http://www.jenitennison.com/xslt/xspec"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    exclude-result-prefixes="xs math"
    version="3.0">
    <xsl:param name="tempdir" select="resolve-uri('xspec-temp-files/')"/>
    <xsl:template match="/">
        <xsl:variable name="collection" select="collection($tempdir || '?select=*-result.xml;on-error=ignore')"/>
        <xsl:variable name="single-reports" as="element()*">
            <xsl:for-each select="$collection/x:report">
                <xsl:variable name="name" select="@xspec/tokenize(., '/')[last()]"/>
                <xsl:variable name="tests" select=".//x:scenario/x:test"/>
                <xsl:variable name="passed" select="count($tests[@successful = 'true'])"/>
                <xsl:variable name="failed" select="count($tests[@successful = 'false'])"/>
                <xsl:variable name="pending" select="count($tests[@pending])"/>
                <xsl:variable name="total" select="sum(($passed, $failed, $pending))"/>
                <report xspec="{@xspec}" 
                    name="{$name}"
                    passed="{$passed}"
                    failed="{$failed}"
                    pending="{$pending}"
                    missing="0"
                    total="{$total}"
                    />
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="failure-file" select="resolve-uri('failures.txt', $tempdir)"/>
        <xsl:variable name="failure-xspecs" select="
            if (unparsed-text-available($failure-file)) 
            then unparsed-text($failure-file) => tokenize(';') 
            else ()
            "/>
        <xsl:variable name="failure-xspecs" select="$failure-xspecs ! normalize-space(.) ! replace(., '\s|\n|\r', '')[. != '']"/>
        
        <xsl:variable name="missing-xspecs" select="$failure-xspecs[not(. = $single-reports/@xspec/normalize-space(.))]"/>
        <xsl:variable name="missing-reports" as="element()*">
            <xsl:for-each select="$missing-xspecs">
                <xsl:variable name="name" select="tokenize(., '/')[last()]"/>
                <xsl:variable name="xspec" select="doc(.)"/>
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
                <report xpsec="{.}" 
                    name="{$name}"
                    passed="0"
                    failed="0"
                    pending="{$pending-count}"
                    missing="{$test-count}"
                    total="{$test-count + $pending-count}"
                />
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:variable name="single-reports" as="element()*" select="$single-reports, $missing-reports"/>
        
        
        <xsl:variable name="sum_failures" select="sum($single-reports/@failed)"/>
        <xsl:variable name="sum_passed" select="sum($single-reports/@passed)"/>
        <xsl:variable name="sum_pending" select="sum($single-reports/@pending)"/>
        <xsl:variable name="sum_missing" select="sum($single-reports/@missing)"/>
        <xsl:variable name="sum_total" select="sum($single-reports/@total)"/>
        
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <title xsl:expand-text="yes"
                    >Summary Report of XSpec Maven plugin (passed: {$sum_passed} / pending: {$sum_pending} / failed: {$sum_failures} / missing: {$sum_missing} / total: {$sum_total})</title>
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
                            <th class="totals {'emphasis'[$sum_missing gt 0]}">missing: {$sum_missing}</th>
                            <th class="totals">total: {$sum_total}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:for-each select="$single-reports" expand-text="yes">
                            <tr class="{
                                if (@failed > 0) 
                                then 'failed' 
                                else if (@missing > 0) 
                                then 'missing' 
                                else if (@passed = 0) 
                                then 'pending' 
                                else 'successful'
                                }">
                                <th>
                                    <a href="{@name}-result.html">
                                        <xsl:value-of select="@name"/>
                                    </a>
                                </th>
                                <th class="totals">{@passed}</th>
                                <th class="totals">{@pending}</th>
                                <xsl:variable name="failed" select="@failed"/>
                                <th class="totals {'emphasis'[$failed > 0]}">{$failed}</th>
                                <xsl:variable name="missing" select="@missing"/>
                                <th class="totals {'emphasis'[$missing > 0]}">{$missing}</th>
                                <th class="totals">{@total}</th>
                            </tr>
                        </xsl:for-each>
                    </tbody>
                </table>
                
                
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>