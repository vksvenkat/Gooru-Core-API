<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
	<xsl:for-each select="node[contains(@type, 'Taxonomy')]">
		<taxonomy>
		<xsl:for-each select="node[contains(@type, 'Subject')]">
			<xsl:sort select="@order"/>
			<subject>
				<xsl:attribute name="label">
					<xsl:value-of select="@label"/>
				</xsl:attribute>
				<xsl:attribute name="codeId">
					<xsl:value-of select="@codeId"/>
				</xsl:attribute>
				<xsl:attribute name="order">
					<xsl:value-of select="@order"/>
				</xsl:attribute>
				<xsl:attribute name="gooru_code">
					<xsl:value-of select="@code"/>
				</xsl:attribute>
				<xsl:for-each select="node[contains(@type, 'Course')]">
					<xsl:sort select="@order"/>
					<course>
						<xsl:attribute name="label">
							<xsl:value-of select="@label"/>
						</xsl:attribute>
						<xsl:attribute name="codeId">
							<xsl:value-of select="@codeId"/>
						</xsl:attribute>
						<xsl:attribute name="order">
							<xsl:value-of select="@order"/>
						</xsl:attribute>
						<xsl:attribute name="gooru_code">
							<xsl:value-of select="@code"/>
						</xsl:attribute>
						<xsl:for-each select="node[contains(@type, 'Unit')]">
							<xsl:sort select="@order"/>
							<unit>
								<xsl:attribute name="label">
									<xsl:value-of select="@label"/>
								</xsl:attribute>
								<xsl:attribute name="codeId">
									<xsl:value-of select="@codeId"/>
								</xsl:attribute>
								<xsl:attribute name="order">
									<xsl:value-of select="@order"/>
								</xsl:attribute>
								<xsl:attribute name="gooru_code">
									<xsl:value-of select="@code"/>
								</xsl:attribute>
								<xsl:for-each select="node[contains(@type, 'Topic')]">
									<xsl:sort select="@order"/>
									<topic>
										<xsl:attribute name="label">
											<xsl:value-of select="@label"/>
										</xsl:attribute>
										<xsl:attribute name="codeId">
											<xsl:value-of select="@codeId"/>
										</xsl:attribute>
										<xsl:attribute name="order">
											<xsl:value-of select="@order"/>
										</xsl:attribute>
										<xsl:attribute name="gooru_code">
											<xsl:value-of select="@code"/>
										</xsl:attribute>
										<xsl:for-each select="node[contains(@type, 'Lesson')]">
											<xsl:sort select="@order"/>
											<lesson>
												<xsl:attribute name="label">
													<xsl:value-of select="@label"/>
												</xsl:attribute>
												<xsl:attribute name="codeId">
														<xsl:value-of select="@codeId"/>
												</xsl:attribute>
												<xsl:attribute name="order">
													<xsl:value-of select="@order"/>
												</xsl:attribute>
												<xsl:attribute name="gooru_code">
													<xsl:value-of select="@code"/>
												</xsl:attribute>
											</lesson>
										</xsl:for-each>
									</topic>
								</xsl:for-each>
							</unit>
						</xsl:for-each>
					</course>
				</xsl:for-each>
			</subject>
		</xsl:for-each>
		</taxonomy>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2008. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="file:///c:/Tomcat5.5/webapps/gooru/taxonomy/prod/1.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal"
		          customvalidator="">
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