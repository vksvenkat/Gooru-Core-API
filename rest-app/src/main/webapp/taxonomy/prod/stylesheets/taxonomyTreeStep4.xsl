<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<xsl:output method="html" encoding="utf-8" omit-xml-declaration="yes" standalone="no" indent="no" media-type="text/html" />
<xsl:template match="/">
	<xsl:call-template name="nodes">
		<xsl:with-param name="node" select="/root" />
	</xsl:call-template>
</xsl:template>
<xsl:template name="nodes">
	<xsl:param name="node" />
	<ul>
	<xsl:for-each select="$node/item">
		<xsl:variable name="children" select="count(./item) &gt; 0" />
		<li>
			<xsl:attribute name="class">
				<xsl:if test="position() = last()">jstree-last </xsl:if>
				<xsl:choose>
					<xsl:when test="@state = 'open'">jstree-open </xsl:when>
					<xsl:when test="$children or @hasChildren or @state = 'closed'">jstree-closed </xsl:when>
					<xsl:otherwise>jstree-leaf </xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="@class" />
			</xsl:attribute>
			<xsl:for-each select="@*">
				<xsl:if test="name() != 'class' and name() != 'state' and name() != 'hasChildren'">
					<xsl:attribute name="{name()}"><xsl:value-of select="." /></xsl:attribute>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="content/name">


					

					<xsl:copy-of select="./child::node()" />

			</xsl:for-each>
			<xsl:if test="$children or @hasChildren"><xsl:call-template name="nodes"><xsl:with-param name="node" select="current()" /></xsl:call-template></xsl:if>
		</li>
	</xsl:for-each>
	</ul>
</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2008. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="step3.xml" htmlbaseurl="" outputurl="Output.xml" processortype="internal" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml=""
		          commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator=""/>
	</scenarios>
	<MapperMetaTag>
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
		<MapperBlockPosition></MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->