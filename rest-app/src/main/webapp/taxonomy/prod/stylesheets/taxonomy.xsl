<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="html" indent="yes" omit-xml-declaration="yes" />
	
	<xsl:key name="subject" match="subject" use="@name"/>
	
	<xsl:template match="/">
			<xsl:for-each select="taxonomy">
			<div>
			<iframe id="resultIframe" frameborder="0" name="resultIframe" scrolling="auto" style="width:100%;height: 1250px;display:none;margin-top:10px;">
			</iframe>
			<xsl:variable name="vLowercaseChars_CONST" select="'abcdefghijklmnopqrstuvwxyz'"/> 
	        <xsl:variable name="vUppercaseChars_CONST" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>

			<div class="universal">
				<table cellspacing="0" cellpadding="0" >
					<tr>
						<td width="15%" valign="top">
							
							<div id="historyDiv" style="margin:-20px 15px 12px 3px;font-size:9pt;text-align:left; float:left;">
								<b>Classplan Library View</b>
							</div>
							
							<div style="background-color:rgb(245,246,249);padding:8px 0px 10px 15px;font-family:arial;font-size:10pt;border:1px solid #E5E5E5">
								<form>
										<div style="margin-bottom:10px;">
											<div style="font-weight:bold;padding-bottom:5px;">Curriculum</div>
												<div style="color:#0000cc"><input type="checkbox" name="Science Contents" value="Science Contents" />Science Contents...</div>
												<div style="color:#0000cc"><input type="checkbox" name="Common Core" value="Common Core" />Common Core S...</div>
												<div style="color:#0000cc"><input type="checkbox" name="Science Contents" value="Science Contents" />New York State...</div>
												<div style="color:#0000cc"><input type="checkbox" name="Science Contents" value="Science Contents" />CBSE</div>
												<div style="color:#0000cc"><input type="checkbox" name="Science Contents" value="Science Contents" />ICSE</div>
												<div style="color:#0000cc"><input type="checkbox" name="Science Contents" value="Science Contents" />Others</div>
										</div>
										<div style="margin-bottom:10px;">
											<div style="font-weight:bold;padding-bottom:5px;">Grade</div>
												<xsl:for-each select="//grade">
													<div style="color:#0000cc" >
														<input type="checkbox" name="grade" onClick="setGradeStatus(this.form);">
															<xsl:attribute name="value">grade<xsl:value-of select="@name"/></xsl:attribute>
														</input>
														<xsl:value-of select="@name"/>
													</div>
												</xsl:for-each>
										</div>
										<div style="margin-bottom:10px;">
											<div style="font-weight:bold;padding-bottom:5px;">Subject</div>
											
											<xsl:for-each select="//subject[generate-id() = generate-id(key('subject',normalize-space(@name))[1])]">
												<xsl:sort select="." />
													<div style="color:#0000cc" >
														<input type="checkbox" name="subject" onClick="setSubjectStatus(this.form);">
															<xsl:attribute name="value"><xsl:value-of select="translate(substring(@name,1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/><xsl:value-of select="translate(substring(@name,2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/></xsl:attribute>
														</input>
														<xsl:value-of select="translate(substring(@name,1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/><xsl:value-of select="translate(substring(@name,2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/>
													</div>
											</xsl:for-each>
										</div>
										<div style="margin-bottom:10px;">
											<div style="font-weight:bold;padding-bottom:5px;">Owner</div>
												<div style="color:#0000cc"><input type="checkbox" name="Science Contents" value="Science Contents" />Chris Martin</div>
												<div style="color:#0000cc"><input type="checkbox" name="Science Contents" value="Science Contents" />John Doe</div>
												<div style="color:#0000cc"><input type="checkbox" name="Science Contents" value="Science Contents" />Mallory Moser</div>
										</div>
										<div style="margin-bottom:10px;">
											<div style="font-weight:bold;padding-bottom:5px;">Rating</div>
												<div style="color:#0000cc" ><input type="checkbox" name="Science Contents" value="Science Contents" />5 Star</div>
												<div style="color:#0000cc" ><input type="checkbox" name="Science Contents" value="Science Contents" />4 Star</div>
												<div style="color:#0000cc" ><input type="checkbox" name="Science Contents" value="Science Contents" />3 Star</div>
												<div style="color:#0000cc" ><input type="checkbox" name="Science Contents" value="Science Contents" />2 Star</div>
												<div style="color:#0000cc" ><input type="checkbox" name="Science Contents" value="Science Contents" />1 Star</div>
										</div>
										<div style="margin-bottom:10px;">
											<span style="cursor:pointer; text-decoration:underline; color:blue;" onclick="callStructuredSearch('all','ALL_CLASSPLANS','classic');">View All</span>
										</div>
								</form>
							</div>
						</td>
						<td valign="top">
							<div class="taxonomyContent" style="margin-bottom:4px;border-top:1px solid #E5E5E5;border-left:1px solid #E5E5E5;overflow:hidden">
								<xsl:for-each select="grade">
									<xsl:for-each select="subject">
										<div>
											<xsl:attribute name="class">taxnWrapper <xsl:value-of select="normalize-space(@name)"/> subjectandgradediv grade<xsl:value-of select="../@name"/> </xsl:attribute>
											<div class='taxnTitle heading_topic'>Grade <xsl:value-of select="../@name"/> - <xsl:value-of select="translate(substring(normalize-space(@name),1,1),$vLowercaseChars_CONST,$vUppercaseChars_CONST)"/><xsl:value-of select="translate(substring(normalize-space(@name),2),$vUppercaseChars_CONST,$vLowercaseChars_CONST)"/></div>
											<div>
												<xsl:attribute name="id">taxonomy<xsl:value-of select="../@name"/><xsl:value-of select="normalize-space(@name)"/></xsl:attribute>
												<ul>
													<xsl:attribute name="class">taxCommonClass</xsl:attribute>
													<xsl:for-each select="unit">
														<li class='taxnTopc'>
															<img src='images/icon_taxnTopc.png' />
															<xsl:value-of select="normalize-space(@name)"/>
														</li>
														<xsl:for-each select="lesson">
															<li class='taxnlsson'>
															<img src='images/icon_taxnlsson.png' />
															<xsl:choose>
																<xsl:when test="@id">
																	<a style='text-decoration:none'>
																	<xsl:attribute name="href">javascript:previewClassPlan('<xsl:value-of select='@id'/>');</xsl:attribute>
																		<span class='taxnlssonName'><xsl:value-of select="normalize-space(@name)"/></span>
																	</a>
																</xsl:when>
																<xsl:otherwise>
																	<span style='color:#000000' class='taxnlssonName'>
																		<xsl:value-of select="normalize-space(@name)"/>	
																	</span>
																</xsl:otherwise>
															</xsl:choose>
															</li>
														</xsl:for-each>
													</xsl:for-each>
												</ul>
											</div>
										</div>
									</xsl:for-each>
								</xsl:for-each>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</xsl:for-each>
	</xsl:template>
</xsl:stylesheet> 