<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<root>
			<xsl:for-each select="//grade">
				<item>
					<xsl:attribute name="id">					
						<xsl:call-template name="string-replace-all">
	     					 <xsl:with-param name="text" select="@gooru_code" />
	    					 <xsl:with-param name="replace" select="'.'" />
	     					 <xsl:with-param name="by" select="'_'" />
	    				</xsl:call-template>
    				</xsl:attribute>	
    				<content><name><xsl:value-of select="./@label"></xsl:value-of></name></content>
					<xsl:for-each select=".//subject">
						<item>
							<xsl:attribute name="id">					
								<xsl:call-template name="string-replace-all">
	     						 <xsl:with-param name="text" select="@gooru_code" />
	    						 <xsl:with-param name="replace" select="'.'" />
	     						 <xsl:with-param name="by" select="'_'" />
	    						</xsl:call-template>
    						</xsl:attribute>
							<content><name><xsl:value-of select="./@label"></xsl:value-of></name></content>
							<xsl:for-each select=".//unit">
								<item>
									<xsl:attribute name="id">					
										<xsl:call-template name="string-replace-all">
	     								 <xsl:with-param name="text" select="@gooru_code" />
	    								 <xsl:with-param name="replace" select="'.'" />
	     								 <xsl:with-param name="by" select="'_'" />
	    								</xsl:call-template>
    								</xsl:attribute>
									<content><name><xsl:value-of select="./@label"></xsl:value-of></name></content>
									<xsl:for-each select=".//topic">
										<item>
											<xsl:attribute name="id">					
												<xsl:call-template name="string-replace-all">
					     						 <xsl:with-param name="text" select="@gooru_code" />
					    						 <xsl:with-param name="replace" select="'.'" />
					     						 <xsl:with-param name="by" select="'_'" />
					    						</xsl:call-template>
				    						</xsl:attribute>
											<content><name><xsl:value-of select="./@label"></xsl:value-of></name></content>
											<xsl:for-each select=".//lesson">
												<item>
													<xsl:attribute name="id">					
														<xsl:call-template name="string-replace-all">
							     						 <xsl:with-param name="text" select="@gooru_code" />
							    						 <xsl:with-param name="replace" select="'.'" />
							     						 <xsl:with-param name="by" select="'_'" />
							    						</xsl:call-template>
							    					</xsl:attribute>
													<content><name><xsl:value-of select="./@label"></xsl:value-of></name></content>
												</item>
											</xsl:for-each>
										</item>
									</xsl:for-each>
								</item>
							</xsl:for-each>
						</item>
					</xsl:for-each>
				</item>
			</xsl:for-each>
		</root>
	</xsl:template>
	<xsl:template name="string-replace-all">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text,$replace)" />
        <xsl:value-of select="$by" />
        <xsl:call-template name="string-replace-all">
          <xsl:with-param name="text"
          select="substring-after($text,$replace)" />
          <xsl:with-param name="replace" select="$replace" />
          <xsl:with-param name="by" select="$by" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
 </xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2008. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="step2.xml" htmlbaseurl="" outputurl="step3.xml" processortype="saxon8" useresolver="no" profilemode="0" profiledepth="" profilelength="" urlprofilexml=""
		          commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator="">
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bSchemaAware" value="true"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="fatal"/>
		</scenario>
	</scenarios>
	<MapperMetaTag>
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
		<MapperBlockPosition></MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->