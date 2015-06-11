<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="html" indent="no" omit-xml-declaration="yes" />
	<xsl:key name="subject" match="subject" use="@label"/>
	<xsl:variable name="apos">'</xsl:variable>
	<xsl:template match="/">
		<xsl:for-each select="taxonomy">
			<html xmlns="http://www.w3.org/1999/xhtml">
				<head>
					<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
					<title>Gooru</title>
					<script type="text/javascript" src="scripts/classplan/taxonomy.js"></script>
					<script>
						<xsl:comment><![CDATA[

						//Script goes here

						]]></xsl:comment>
					</script>
				</head>
				<body>
					<script>
				var grades = new Array();
				var subjects = new Array();
				var i = 0;
				var j = 0;
					</script>
					<div>
						<xsl:variable name="vLowercaseChars_CONST" select="'abcdefghijklmnopqrstuvwxyz'"/>
						<xsl:variable name="vUppercaseChars_CONST" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
						<div class="universal" style="padding:0px;">
							<table width="100%"  cellspacing="0" cellpadding="0" >
								<tr>
									<td valign="top" class="taxonomyContentBorder">
										<form style="padding:0px; margin:0px;" name="taxonomyFiltersForm">
										<div id="taxonomyContent">
											<xsl:for-each select="grade">
												<script>
													grades[i] = "grade<xsl:value-of select="normalize-space(@label)"/>";
													i = i + 1;
												</script>
												<xsl:for-each select="subject">
													<script>
														subjects[j] = "<xsl:value-of select="translate(substring(normalize-space(@label),1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/><xsl:value-of select="translate(substring(normalize-space(@label),2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/>";
														j = j + 1;
													</script>
													<xsl:variable name="div-name">grade<xsl:value-of select="../@label"/><xsl:value-of select="translate(substring(normalize-space(@label),1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/><xsl:value-of select="translate(substring(normalize-space(@label),2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/></xsl:variable>
													<div class="showHideGradeSubject" style="display: block;">
														<xsl:attribute name="id"><xsl:value-of select="$div-name"/></xsl:attribute>
														<div class="headerBar">
															<xsl:attribute name="onmouseover">headerBarHover(this,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
															<xsl:attribute name="onmouseout">headerBarHoverOut(this,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
															<div style="float:left;color:#666666;font-family:tahoma;font-size:16px;font-weight:bold">Grade <xsl:value-of select="../@label"/> - <xsl:value-of select="translate(substring(normalize-space(@label),1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/><xsl:value-of select="translate(substring(normalize-space(@label),2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/></div>
															<div align="right" class="filterHoverFont" style="visibility:hidden">
																<xsl:attribute name="id"><xsl:value-of select="$div-name"/>Filter</xsl:attribute>
																<table width="50%" cellpadding="0" cellspacing="0" border="0"><tr>
																	<td width="100%"> </td><td width="1%" class="filterHeaders">Grade:</td>
																	<xsl:for-each select="//grade">
																		<td width="1%" class="filterContent">
																			<input type="checkbox" name="grade">
																				<xsl:attribute name="onClick">filterGradeSubject(document.taxonomyFiltersForm.grade, document.taxonomyFiltersForm.subject,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
																				<xsl:attribute name="value">grade<xsl:value-of select="@label"/></xsl:attribute>
																			</input>
																		</td>
																		<td class="filterContentLabel"><xsl:value-of select="@label"/></td>
																	</xsl:for-each>
																	
																	<td style="padding-left:5px; padding-right:0px;"><img src="images/classplan/taxonomy/headerBarFilter_dividor.png"/></td>

																	<td width="1%" class="filterHeaders">Subject:</td>
																	<xsl:for-each select="//subject[generate-id() = generate-id(key('subject',normalize-space(@label))[1])]">
																		<xsl:sort select="." />
																		<td width="1%" class="filterContent">
																			<input type="checkbox" name="subject">
																				<xsl:attribute name="onClick">filterGradeSubject(document.taxonomyFiltersForm.grade, document.taxonomyFiltersForm.subject,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
																				<xsl:attribute name="value">
																					<xsl:value-of select="translate(substring(@label,1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/>
																					<xsl:value-of select="translate(substring(@label,2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/>
																				</xsl:attribute>
																			</input>
																		</td>
																		<td class="filterContentLabel">
																			<xsl:value-of select="translate(substring(@label,1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/>
																			<xsl:value-of select="translate(substring(@label,2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/>
																		</td>
																	</xsl:for-each>

																	<td style="padding-left:5px; padding-right:0px;"><img src="images/classplan/taxonomy/headerBarFilter_dividor.png"/></td>
																	<td width="1%" class="filterHeaders"><img style="cursor:pointer; text-decoration:underline; color:blue;" onclick="showAllDivs(document.taxonomyFiltersForm.grade, document.taxonomyFiltersForm.subject);" src="images/classplan/taxonomy/btn_viewAll.png"/></td>
																</tr></table>
															</div>
														</div>
														<div style="padding-left:15px;">
															<xsl:attribute name="id">taxonomy<xsl:value-of select="../@label"/><xsl:value-of select="normalize-space(@label)"/></xsl:attribute>
															<xsl:variable name="allowed-unitItems">
																<xsl:value-of select="((count(descendant::*) div 3))"/>
															</xsl:variable>
															<xsl:variable name="div-1-set" select="descendant::*[position() &lt;= ($allowed-unitItems * 1)]" />
															<xsl:variable name="div-name1"><xsl:value-of select="$div-name"/><xsl:text>-1</xsl:text></xsl:variable>
															<div style="width:33%; float:left;">
																<xsl:attribute name="id">
																	<xsl:value-of select="$div-name1"/>
																</xsl:attribute>
																<xsl:for-each select="$div-1-set[name()='unit']">
																	<xsl:variable name="unit-name" select="@label"/>
																	<xsl:variable name="children-in" select="count($div-1-set[ancestor::*[@label=$unit-name]])"/>
																	<xsl:variable name="children-all" select="count(descendant::*)"/>
																	<xsl:if test="$children-in*3 &gt;= $children-all">
																		<xsl:apply-templates select="."/>
																	</xsl:if>
																</xsl:for-each>
															</div>
															<xsl:variable name="div-2-set" select="descendant::*[(position() &lt;= ($allowed-unitItems * 2)) and (position() &gt; ($allowed-unitItems * 1))]" />
															<xsl:variable name="div-name2"><xsl:value-of select="$div-name"/><xsl:text>-2</xsl:text></xsl:variable>
															<div style="width:33%; float:left;">
																<xsl:attribute name="id">
																	<xsl:value-of select="$div-name2"/>
																</xsl:attribute>
																<xsl:variable name="first-node" select="$div-2-set[position()=1]"/>
																<xsl:for-each select="$first-node">
																	<xsl:variable name="unit-node" select="ancestor::unit"/>
																	<xsl:for-each select="$unit-node">
																		<xsl:variable name="unit-name" select="@label"/>
																		<xsl:variable name="children-in" select="count($div-1-set[ancestor::*[@label=$unit-name]])"/>
																		<xsl:variable name="children-all" select="count(descendant::*)"/>
																		<xsl:choose>
																			<xsl:when test="$children-in*3 &lt; $children-all">
																				<xsl:apply-templates select="."/>
																			</xsl:when>
																			<xsl:otherwise>
																				<script>
																				storeDiv2Hits("off-<xsl:value-of select="$div-name2"/>");
																				</script>
																			</xsl:otherwise>
																		</xsl:choose>
																	</xsl:for-each>
																</xsl:for-each>
																<xsl:for-each select="$div-2-set[name()='unit']">
																	<xsl:variable name="unit-name" select="@label"/>
																	<xsl:variable name="children-in" select="count($div-2-set[ancestor::*[@label=$unit-name]])"/>
																	<xsl:variable name="children-all" select="count(descendant::*)"/>
																	<xsl:choose>
																		<xsl:when test="$children-in = $children-all">
																			<xsl:apply-templates select="."/>
																			<script>
																			storeDiv2Hits("on-<xsl:value-of select="$div-name2"/>");
																			</script>
																		</xsl:when>
																		<xsl:when test="$children-in*2 &gt;= $children-all">
																			<xsl:apply-templates select="."/>
																			<script>
																			storeDiv2Hits("on-<xsl:value-of select="$div-name2"/>");
																			</script>
																		</xsl:when>
																		<xsl:otherwise>
																				<script>
																				storeDiv2Hits("off-<xsl:value-of select="$div-name2"/>");
																				</script>
																		</xsl:otherwise>
																	</xsl:choose>
																</xsl:for-each>
															</div>
															<xsl:variable name="div-name3"><xsl:value-of select="$div-name"/><xsl:text>-3</xsl:text></xsl:variable>
															<div style="width:33%; float:left;">
																<xsl:attribute name="id">
																	<xsl:value-of select="$div-name3"/>
																</xsl:attribute>
																<xsl:variable name="div-3-set" select="descendant::*[(position() &lt;= ($allowed-unitItems * 3)) and (position() &gt; ($allowed-unitItems * 2))]" />
																<xsl:variable name="first-node" select="$div-3-set[position()=1]"/>
																<xsl:for-each select="$first-node">
																	<xsl:variable name="unit-node" select="ancestor::unit"/>
																	<xsl:for-each select="$unit-node">
																		<xsl:variable name="unit-name" select="@label"/>
																		<xsl:variable name="children-in" select="count($div-3-set[ancestor::*[@label=$unit-name]])"/>
																		<xsl:variable name="children-all" select="count(descendant::*)"/>
																		<xsl:if test="$children-in*2 &gt; $children-all">
																			<xsl:apply-templates select="."/>
																		</xsl:if>
																	</xsl:for-each>
																</xsl:for-each>
																<xsl:for-each select="$div-3-set[name()='unit']">
																	<xsl:apply-templates select="."/>
																</xsl:for-each>
															</div>
															<div style="font-size:6pt; color:#FFF; padding-top:7px; padding-bottom:8px;">|</div></div></div>
												</xsl:for-each>
											</xsl:for-each></div>
									</form>
									</td>
								</tr>
							</table></div></div>
				<script>
				handleDiv2Hits();
				</script>
				</body>
			</html>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="unit" >
		<div style="font-size:6pt; color:#FFF; padding-top:0px; padding-bottom:0px; width:100%; float:left">|</div>
		<div class="taxonomyUnit">
			<div style="float:left; padding-right:10px; vertical-align:middle">
				<img src="images/classplan/taxonomy/icon_unit.png" />
			</div>
			<div style="font-family:Arial; font-weight:bold; font-size:11pt; color:#4f9748;">Unit: <xsl:value-of select="normalize-space(@label)"/>
			</div>
		</div>
		<div style="padding-left:20px; padding-right:20px;">
			<div style="background-image:url(images/classplan/taxonomy/h_dotted.png); background-repeat:repeat-x; background-position:center; font-size:6pt; color:#FFF; padding-top:0px; padding-bottom:0px;">|</div></div>
		<div style="width:100%; float:left;">
			<ul class="taxCommonClass" style="">
				<xsl:apply-templates select="topic" mode="demanded"/>
			</ul>
		</div>
	</xsl:template>
	<xsl:template match="topic" mode="demanded">
		<li class="taxonomyTopic" >
			<xsl:value-of select="normalize-space(@label)"/>
			<ul class="lessonList">
				<xsl:for-each select="lesson">
					<li class="taxonomyLesson" onmouseover="this.className='taxonomyLessonHover';" onmouseout="this.className='taxonomyLesson';">
						<xsl:choose>
							<xsl:when test="@id">
								<xsl:attribute name="onclick">javascript:showClassPlan('<xsl:value-of select='translate(@label,$apos,"")'/>');</xsl:attribute>
								<xsl:value-of select="normalize-space(@label)"/>
							</xsl:when>
							<xsl:otherwise>
								<span style='color:#000000' class='taxnlssonName'>
									<xsl:value-of select="normalize-space(@label)"/>
								</span>
							</xsl:otherwise>
						</xsl:choose>
					</li>
				</xsl:for-each>
			</ul>
		</li>
	</xsl:template>
</xsl:stylesheet>
<!-- Stylus Studio meta-information - (c) 2004-2008. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="Step3-Output.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml=""
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
