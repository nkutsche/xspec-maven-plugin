<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet exclude-result-prefixes="#all" version="1.0" xmlns:x="http://www.jenitennison.com/xslt/xspec"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


    <!--
		Detect test type from XSpec file
		Output XML structure is for Ant <xmlproperty> task.
	-->
    <xsl:template match="/">
        <test>
            <type>
                <xsl:variable name="xspecRoot" select="x:description"/>
                <xsl:choose>
                    <xsl:when test="$xspecRoot/@stylesheet">
                        <xsl:text>t</xsl:text>
                    </xsl:when>
                    <xsl:when test="$xspecRoot/@schematron">
                        <xsl:text>s</xsl:text>
                    </xsl:when>
                    <xsl:when test="$xspecRoot/@query-at">
                        <xsl:text>q</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:message terminate="yes">
                            <xsl:text>Can not detect the test type of the XSpec file.</xsl:text>
                        </xsl:message>
                    </xsl:otherwise>
                </xsl:choose>
            </type>
        </test>
    </xsl:template>
</xsl:stylesheet>
