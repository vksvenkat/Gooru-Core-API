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
				var gradeHtml = "";
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
													<div class="resourceHideGradeSubject showHideGradeSubject" style="display: block;">
														<xsl:attribute name="id"><xsl:value-of select="$div-name"/></xsl:attribute>
														<div class="headerBar">
															<xsl:attribute name="onmouseover">headerBarHover(this,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
															<xsl:attribute name="onmouseout">headerBarHoverOut(this,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
															<div style="float:left;color:#515151;font-family:arial;font-size:18px;font-weight:normal;padding-top: 20px;"> <xsl:value-of select="../@label"/> - <xsl:value-of select="translate(substring(normalize-space(@label),1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/><xsl:value-of select="translate(substring(normalize-space(@label),2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/></div>
															<div align="right" class="filterHoverFont" style="visibility:hidden">
																<xsl:attribute name="id"><xsl:value-of select="$div-name"/>Filter</xsl:attribute>
																<table width="61%" cellpadding="0" cellspacing="0" border="0"><tr>
																	<td width="40%" class="filterContent">
																	<input class="middle-school" type="checkbox"  name="middle-school">
																	    <xsl:attribute name="onClick">handleCheckBoxClick('middle-school',document.taxonomyFiltersForm.grade, document.taxonomyFiltersForm.subject,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
																		Middle School
																	</input> 
																	<input class="high-school" type="checkbox"  name="high-school">
																	    <xsl:attribute name="onClick">handleCheckBoxClick('high-school',document.taxonomyFiltersForm.grade, document.taxonomyFiltersForm.subject,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
																	High School																	
																	</input>
																	</td>
																	<td class="filterContent filterGrade" style="display:none;"></td>
																	    <xsl:for-each select="//grade">
																		<td width="1%" class="filterContent" style="display:none;">
 																			<input type="checkbox" class="gradeFilter1" style="display:none;" name="grade">
																				<xsl:attribute name="onClick">filterGradeSubject(document.taxonomyFiltersForm.grade, document.taxonomyFiltersForm.subject,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
																				<xsl:attribute name="value">grade<xsl:value-of select="@label"/></xsl:attribute>
																			</input>
 																		</td>
																		<td class="filterContentLabel"><span style="display:none;"> <xsl:value-of  select="@label"  /></span></td>
																	</xsl:for-each> 
																	
																	<td style="padding-left:5px; padding-right:0px;padding-top: 20px;"><img src="images/classplan/taxonomy/headerBarFilter_dividor.png"/></td>

																	<td width="20%" class="msFilter" >
																		<select class="mathsFilter">
																		<option>Maths</option>
																		<option>All Math</option>
																		<option>Middle School Math I</option>
																		<option>Middle School Math II</option>
																		<option>Pre-Algebra</option>
																		<option>Algebra I</option>																																																																																											
																		</select>
																		<select class="scienceFilter">
																		<option>Science</option>
																		<option>Integrated Science</option>
																		<option>Earth Science</option>
																		<option>Life Science</option>
																		<option>Physical Science</option>
																		</select>
																	</td>
																					
																	<xsl:for-each select="//subject[generate-id() = generate-id(key('subject',normalize-space(@label))[1])]">
																		<xsl:sort select="." />
																		<td class="filterContent">
																												
																			 	 <input type="checkbox" style="display:none;" name="subject">
																				<xsl:attribute name="onClick">filterGradeSubject(document.taxonomyFiltersForm.grade, document.taxonomyFiltersForm.subject,'<xsl:value-of select="$div-name"/>Filter');</xsl:attribute>
																				<xsl:attribute name="value">
																					<xsl:value-of select="translate(substring(@label,1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/>
																					<xsl:value-of select="translate(substring(@label,2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/>
																				</xsl:attribute>
																			</input>
																		</td>
																		<td class="filterContentLabel">
																		  <span style="display:none;">
																			<xsl:value-of select="translate(substring(@label,1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/>
																			<xsl:value-of select="translate(substring(@label,2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/>
																		</span>
																		</td>
																	</xsl:for-each>

																	<td style="padding-left:5px; padding-right:0px;padding-top: 20px;"><img src="images/classplan/taxonomy/headerBarFilter_dividor.png"/></td>
																	<td width="1%" class="filterHeaders" ><img style="cursor:pointer; text-decoration:underline; color:blue; padding-top:20px;" onclick="showAllDivs(document.taxonomyFiltersForm.grade, document.taxonomyFiltersForm.subject);" src="images/classplan/taxonomy/btn_viewAll.png"/></td>
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
															<div style="width:32%; float:left;">
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
															<div style="width:32%; float:left;">
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
															<div style="width:32%; float:left;">
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
							</table></div>
											
							</div>
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
			<div style="font-family:Arial; font-weight:500; font-size:14px; color:#313131;">Unit: <xsl:value-of select="normalize-space(@label)"/>
			</div>
		</div>
		<div style="padding-left:0px; padding-right:40px;">
			<div style="background-position:center; font-size:6pt; color:#FFF; padding-top:0px; padding-bottom:0px;">|</div></div>
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
					<li class="taxonomyLesson taxLess">
						<xsl:choose>
							<xsl:when test="@order">
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

