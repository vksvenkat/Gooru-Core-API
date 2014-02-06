<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/TR/xpath-functions/" >

<xsl:output method="xml" indent="yes"/>	
 <!-- msxsl:script get date -->
   


	<xsl:param name="page-length">350mm</xsl:param>
	<xsl:param name="page-width">210mm</xsl:param>
	<xsl:param name="appPath">C:/Tomcat/webapps/gooruapi/</xsl:param>
	<xsl:param name="learnGuide">classboo</xsl:param>
	<xsl:param name="assetURI"></xsl:param>
	<xsl:param name="date"></xsl:param>
	
	
	
	<xsl:template match="/">

		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

			<fo:layout-master-set>
				<fo:simple-page-master margin="5mm 10mm 5mm 10mm" master-name="PageMaster">
					<xsl:attribute name="page-width">
						<xsl:value-of select="$page-width"/>
						<!--setting the page width -->
					</xsl:attribute>
					<xsl:attribute name="page-height">
						<xsl:value-of select="$page-length"/>
						<!--setting the page height-->
					</xsl:attribute>
					<fo:region-body margin="25mm 0mm 25mm 0mm"/>
					<fo:region-before region-name="page-header" extent="20mm" />
					<fo:region-after  region-name="page-footer" extent="10mm" background-color="white"/>
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="PageMaster">
				<fo:static-content flow-name="page-header">
				<fo:block >
				<fo:table table-layout="fixed" >
				    <fo:table-column column-width="160mm"/>
					<fo:table-column column-width="30mm"/>
					<fo:table-body>
					<fo:table-row>
					<fo:table-cell>
					    <fo:block font-size="26pt" font-family="arial" display-align="center"   color="#bababa" background-position="0,0" background-repeat="repeat" padding-bottom="5px" padding-top="5px" padding-left="5px">
					    <xsl:attribute name="background-image">url(<xsl:value-of select="$appPath"/>images/Background_1px.png)</xsl:attribute>
						
						<xsl:value-of select="$learnGuide"/>
						<xsl:text> | </xsl:text>
						<xsl:value-of select="gooruclassplan/user/firstname"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="gooruclassplan/user/lastname"/>
						
						</fo:block>		
					</fo:table-cell>
					<fo:table-cell >
					<fo:block  text-align="right" border-bottom-color="black" >
					<xsl:attribute name="background-image">url(<xsl:value-of select="$appPath"/>images/Background_1px.png)</xsl:attribute>
						<fo:external-graphic width="105px" height="51.5px">
						<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/gooru.png)</xsl:attribute>
					    </fo:external-graphic>
					</fo:block>
					</fo:table-cell>
					</fo:table-row>
					</fo:table-body>
					</fo:table>
					</fo:block>
				</fo:static-content>
				<fo:static-content flow-name="page-footer">
					<fo:block>
					<fo:table table-layout="fixed">
					<fo:table-column column-width="60mm"/>
					<fo:table-column column-width="60mm"/>
					<fo:table-column column-width="60mm"/>
					<fo:table-body>
					<fo:table-row>
					<fo:table-cell>
					<fo:block font-size="8pt" font-family="arial" color="#aaaaaa" text-align="left">
					<xsl:value-of select="$date"/>
					</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					<fo:block text-align="center">
					<fo:external-graphic>
					<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/ednovo.png)</xsl:attribute>
					</fo:external-graphic>
					</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					<fo:block font-size="8pt" font-family="arial" color="#aaaaaa" text-align="right">
					<xsl:text>Page </xsl:text>
					<fo:page-number/>
					<xsl:text> of </xsl:text>
					<fo:page-number-citation ref-id="endofdoc"/> 
					</fo:block>
					</fo:table-cell>
					</fo:table-row>
					</fo:table-body>
					</fo:table>
					</fo:block>
				    </fo:static-content>
				    <fo:flow flow-name="xsl-region-body">
					<fo:block>
					<xsl:apply-templates select="gooruclassplan"/>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template match="gooruclassplan">
		
		<xsl:apply-templates select="info"/>
		<fo:table space-after="4mm" padding="2mm">
			<fo:table-column column-width="90mm"/>
			<fo:table-column column-width="100mm"/>
			
			<fo:table-body>
				<fo:table-row padding-after="1mm">
				<xsl:apply-templates select="grade"/>
				<xsl:apply-templates select="lesson"/>
				</fo:table-row>

				<fo:table-row padding-after="1mm">
				<xsl:apply-templates select="subject"/>
				<xsl:apply-templates select="topic"/>
				</fo:table-row>

				<fo:table-row padding-after="1mm">
				<xsl:apply-templates select="author"/>
				<xsl:apply-templates select="unit"/>
				</fo:table-row>
			</fo:table-body>
		</fo:table>

		<fo:table>
		<fo:table-column column-width="80mm"/>
		<fo:table-column column-width="10mm"/>
		<fo:table-column column-width="100mm"/>
		<fo:table-body>
		<fo:table-row>
		<fo:table-cell>
		<xsl:apply-templates select="info/lessonobjectives"/>
		</fo:table-cell>
		<fo:table-cell>
		</fo:table-cell>
		<fo:table-cell>
		<xsl:apply-templates select="info/vocabulary"/>
		</fo:table-cell>
		</fo:table-row>
		
		<fo:table-row>
		<fo:table-cell >
		<xsl:for-each select="segments/segment">
			<xsl:if test="title = 'Suggested Study'">
				<xsl:apply-templates select="."/>
			</xsl:if>
			</xsl:for-each>
			</fo:table-cell>
			<fo:table-cell>
		</fo:table-cell>
		<fo:table-cell>
			<xsl:for-each select="segments/segment">
			<xsl:if test="title = 'Homework'">
				<xsl:apply-templates select="."/>
			</xsl:if>
			</xsl:for-each>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
		<fo:table-cell>
			<xsl:for-each select="segments/segment">
			<xsl:if test="title = 'Assessments'">
				<xsl:apply-templates select="."/>
			</xsl:if>
		</xsl:for-each>
</fo:table-cell>
		</fo:table-row>

		</fo:table-body>
		</fo:table>
		<xsl:apply-templates select="segments"/>
	</xsl:template>

	

	<xsl:template match="info">
		<fo:block border-bottom-style="solid" border-bottom-color="rgb(153,153,153)" border-before-width=".1mm" space-after="3mm" font-size="14pt" font-family="arial" font-weight="bold" color="#1076bb" hyphenate="true">
			<xsl:value-of select="lesson"/>
		</fo:block>
		
	</xsl:template>

	<xsl:template match="author">
		<fo:table-cell>
			<fo:block hyphenate="true" >
				<fo:inline font-size="10pt" font-family="arial" font-weight="bold" color="#515151">Author:</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal" font-size="10pt" font-family="arial" color="#535353">
					<xsl:value-of select="firstname"/>
				</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal" font-size="10pt" font-family="arial" color="#535353">
				<xsl:value-of select="lastname"/>
				</fo:inline>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="unit">
		<fo:table-cell>
			<fo:block hyphenate="true" >
				<fo:inline font-size="10pt" font-family="arial" font-weight="bold" color="#515151">Unit:</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal" font-size="10pt" font-family="arial" color="#535353">
					<xsl:value-of select="."/>
				</fo:inline>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<!--<xsl:template match="curriculums">
		<fo:table-cell>
			<fo:block>
				<fo:inline font-weight="bold">Curriculum:</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal">
					<xsl:value-of select="."/>
				</fo:inline>
			</fo:block>
		</fo:table-cell>
	</xsl:template>-->

	<xsl:template match="grade">
		<fo:table-cell>
			<fo:block hyphenate="true">
				<fo:inline font-size="10pt" font-family="arial" font-weight="bold" color="#515151">Grade:</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal" font-size="10pt" font-family="arial" color="#535353">
					<xsl:value-of select="."/>
				</fo:inline>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="topic">
		<fo:table-cell>
			<fo:block hyphenate="true" >
				<fo:inline font-size="10pt" font-family="arial" font-weight="bold" color="#515151">Topic:</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal" font-size="10pt" font-family="arial" color="#535353">
					<xsl:value-of select="."/>
				</fo:inline>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<!--<xsl:template match="instruction">
		<fo:table-cell>
			<fo:block>
				<fo:inline font-weight="bold">Medium:</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal">English</fo:inline>
			</fo:block>
		</fo:table-cell>
	</xsl:template>-->


	<xsl:template match="subject">
		<fo:table-cell>
			<fo:block hyphenate="true" >
				<fo:inline font-size="10pt" font-family="arial" font-weight="bold" color="#515151">Subject:</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal" font-size="10pt" font-family="arial" color="#535353">
					<xsl:value-of select="."/>
				</fo:inline>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="lesson">
		<fo:table-cell>
			<fo:block hyphenate="true" > 
				<fo:inline font-size="10pt" font-family="arial" font-weight="bold" color="#515151">Lesson:</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal" font-size="10pt" font-family="arial" color="#535353">
					<xsl:value-of select="."/>
				</fo:inline>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<!--<xsl:template match="code">
		<fo:table-cell>
			<fo:block>
				<fo:inline font-weight="bold">Code:</fo:inline>
				<xsl:text> </xsl:text>
				<fo:inline font-weight="normal">
					<xsl:value-of select="."/>
				</fo:inline>
			</fo:block>
		</fo:table-cell>
	</xsl:template>-->


	<xsl:template match="lessonobjectives">

		<fo:table padding="2mm" margin-right="5mm" space-after="1mm">
			
			<fo:table-column column-width="90mm"/>
			<fo:table-body >
				<fo:table-row>
					
					<fo:table-cell padding-after="5mm">
						<fo:block font-weight="bold" font-size="10pt" font-family="arial" color="#515151">Lesson Objectives:</fo:block>
						<xsl:text> </xsl:text>
						<fo:block font-weight="normal" font-size="10pt" font-family="arial" wrap-option="wrap" color="#535353" hyphenate="true" linefeed-treatment="preserve">
							<xsl:value-of select="//lessonobjectives"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template match="vocabulary">
		<fo:table padding="2mm" space-after="1mm">
			
			<fo:table-column column-width="100mm"/>
			
			<fo:table-body>
				<fo:table-row>
					
					<fo:table-cell>
						<fo:block font-weight="bold" font-size="10pt" font-family="arial" color="#515151">Vocabulary:</fo:block>
						<xsl:text> </xsl:text>
					
						<fo:block font-weight="normal" font-size="10pt" font-family="arial" color="#535353" hyphenate="true" linefeed-treatment="preserve" wrap-option="wrap">
							<xsl:value-of select="//vocabulary"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>


	<xsl:template match="segment">
		<fo:block padding-after="2" space-after="1mm" hyphenate="true" >
			<fo:inline  font-weight="bold" font-size="10pt" font-family="arial" color="#515151" >
				<xsl:value-of select="title"/>
			</fo:inline>
		</fo:block>


		<fo:block font-weight="normal" font-size="10pt" font-family="arial" color="#535353"  space-after="3mm">
			<xsl:for-each select="resources/resource">
				<fo:block hyphenate="true" wrap-option="wrap" linefeed-treatment="preserve">
					<xsl:apply-templates select="."/>
				</fo:block>
			</xsl:for-each>
		</fo:block>
	</xsl:template>



	<xsl:template match="segments">


		<fo:table table-layout="fixed"   space-after="4mm" font-family="arial" font-size="12pt" border-color="rgb(168,168,168)">
			<xsl:if test="$learnGuide='classbook'">
			
			<fo:table-column column-width="100mm"/>
			<fo:table-column column-width="90mm"/>
			</xsl:if>
			<xsl:if test="$learnGuide!='classbook'">
			
			<fo:table-column column-width="60mm"/>
			<fo:table-column column-width="65mm"/>
			<fo:table-column column-width="30mm"/>
			<fo:table-column column-width="35mm"/>
			</xsl:if>
			<fo:table-header border-width="0mm">
				<fo:table-row display-align="center" padding-before="2pt" text-align="center" font-weight="bold" height="8mm"  color="#4e9746">
					<fo:table-cell >
						<fo:block font-size="12pt" font-family="arial" font-weight="bold">SEGMENT DESCRIPTION</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block font-size="12pt" font-family="arial" font-weight="bold">RESOURCES</fo:block>
					</fo:table-cell>
					<xsl:if test="$learnGuide!='classbook'">
					<fo:table-cell >
						<fo:block font-size="12pt" font-family="arial" font-weight="bold">CLASSTUBE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block font-size="12pt" font-family="arial" font-weight="bold">DURATION</fo:block>
					</fo:table-cell>
					</xsl:if>
				</fo:table-row>
			</fo:table-header>

			<fo:table-body  border-right-style="solid" border-left-style="solid" border-top-style="solid" border-bottom-style="solid"  border-right-width=".1px" border-left-width=".1px" border-top-width=".1px" border-bottom-width=".1px" border-color="rgb(153,153,153)" >
				<xsl:for-each select="segment[type  != 'suggestedstudy' and type != 'homework' and type !='assessment' or not(type)]">
					<fo:table-row>
						<fo:table-cell border-color="rgb(153,153,153)" border-right-style="solid" border-right-width=".1px" border-bottom-style="solid" border-bottom-width=".1px"  >
							
							<fo:block font-family="arial" font-size="9pt" font-weight="bold" hyphenate="true" border-bottom-style="solid" border-bottom-width=".1px" border-bottom-color="rgb(153,153,153)" >
							<fo:block margin-bottom="0mm" margin-left="2mm" margin-right="2mm" margin-top="2mm" color="#515151"  border-left-width="0px" border-bottom-width="0px" border-right-width="0px" border-top-width="0px">
							<xsl:value-of select="position()"/>
							<xsl:text>. </xsl:text>
								<xsl:value-of select="title"/>
								</fo:block>
							</fo:block>
							<fo:block hyphenate="true" border-left-width="0px" border-bottom-width="0px" border-right-width="0px" border-top-width="0px">
								<xsl:variable name="substr" select="replace(description,'&lt;ul&gt;','')"/>
								<xsl:variable name="substr1" select="replace($substr,'&lt;/ul&gt;','')"/>
								<xsl:for-each select="tokenize($substr1,'&lt;/li&gt;')">
									<fo:block font-family="arial" font-size="9pt" margin="0mm 2mm 2mm 2mm" color="#535353"> 
										<xsl:variable name="substr2" select="substring-after(.,'&lt;li&gt;')"/>
										<xsl:if test="string-length($substr2)&gt;0">
										<xsl:text> </xsl:text>
										<xsl:text>&#x2022;</xsl:text>
										<xsl:text> </xsl:text>
										<xsl:value-of select="$substr2"/>
										</xsl:if>
									</fo:block>
								</xsl:for-each>
							</fo:block>
					
						</fo:table-cell>

						<fo:table-cell border-bottom-color="rgb(153,153,153)" border-right-color="rgb(153,153,153)"  border-right-style="solid" border-right-width=".1px" border-bottom-style="solid" border-bottom-width=".1px">
							
							<fo:block font-weight="bold" hyphenate="true" border-bottom-style="solid" border-bottom-width=".1px"  border-bottom-color="rgb(153,153,153)">
						    <fo:block font-family="arial" font-size="9pt" margin="2mm 2mm 0mm 2mm" color="#515151" >
							
							<xsl:value-of select="count(resources/resource)"/>
							
								<xsl:text> Resources</xsl:text>
								</fo:block>
							</fo:block>
							
							<fo:block>
							<xsl:variable name="total" select="count(resources/resource)"/>
								<xsl:for-each select="resources/resource">
								<xsl:variable name="counter" select="position()"/>
									<xsl:if test="$counter &lt; $total">
									<fo:block font-family="arial" font-size="9pt" border-bottom-style="solid" border-bottom-width=".1px" border-bottom-color="rgb(153,153,153)" color="#535353" >
										<xsl:apply-templates select="."/>
									</fo:block>
								</xsl:if>
								<xsl:if test="$counter = $total">
									<fo:block font-family="arial" font-size="9pt" border-bottom-width="0px" border-bottom-color="rgb(153,153,153)" color="#535353" >
										<xsl:apply-templates select="."/>
									</fo:block>
								</xsl:if>
								</xsl:for-each>
							</fo:block>
						
						</fo:table-cell>
						<xsl:if test="$learnGuide!='classbook'">

						<fo:table-cell display-align="center" text-align="center" border-color="rgb(153,153,153)" border-right-style="solid" border-right-width=".1px" border-bottom-style="solid" border-bottom-width=".1px"  >
							
							<xsl:if test="rendition/nativeurl!=''">
								<fo:block font-family="arial" font-size="9pt" hyphenate="true" color="#535353" >
								Video
								<fo:block color="#0066CC" font-family="arial" font-size="9pt">
								<xsl:text>(</xsl:text>
								<xsl:value-of select="rendition/nativeurl"/>
								<xsl:text>)</xsl:text>
								</fo:block>
								<!--<fo:basic-link>
								<xsl:attribute name="external-destination" target="blank">
									<xsl:value-of select="$link"></xsl:value-of>
								</xsl:attribute>
									Video
								</fo:basic-link>
								--></fo:block>
							</xsl:if>
							<xsl:if test="rendition/nativeurl=''">
								<fo:block color="#535353"><xsl:text>-</xsl:text></fo:block>
							</xsl:if>
						</fo:table-cell>

						<fo:table-cell display-align="center" border-color="rgb(153,153,153)" border-bottom-style="solid" border-bottom-width=".1px" >
							<fo:block font-family="arial" font-size="9pt" font-weight="bold" hyphenate="true" text-align="center" color="#535353">
								<xsl:value-of select="duration"/>
								<xsl:text> minutes</xsl:text>
							</fo:block>
						</fo:table-cell>
						</xsl:if>
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>

		<fo:block font-weight="bold" space-after="3mm" color="#515151">Notes:</fo:block>
		
		<fo:block id="endofdoc" font-weight="normal"  font-family="arial" font-size="9pt" linefeed-treatment="preserve"
		white-space-collapse="false" 
          wrap-option="wrap" color="#535353">
			<xsl:value-of select="//notes"/>
		</fo:block>
	</xsl:template>







	<xsl:template match="resource">
		<xsl:if test="@type='ppt/pptx'">
			<fo:table table-layout="fixed" margin="2mm 2mm 0mm 2mm">

				<fo:table-column column-width="10mm"/>
				<xsl:if test="../../type='suggestedstudy' or ../../type='assessment' or ../../type='homework' ">
					<fo:table-column column-width="75mm"/>
				</xsl:if>
				<xsl:if test="../../type!='suggestedstudy' and ../../type!='assessment' and ../../type!='homework' ">
					<fo:table-column column-width="50mm"/>
				</xsl:if>

				<fo:table-body border-left-width="0mm" border-right-width="0mm" border-top-width="0mm" border-bottom-width="0mm">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic height="10px" width="10px">
									<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/PPT.png)</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block hyphenate="true" color="#535353">


								<xsl:value-of select="label"/>
							</fo:block>
							<xsl:if test="instructornotes/start!='' and instructornotes/stop!=''">
								<fo:block font-size="8pt" font-family="arial" color="#535353">
								    <xsl:text>  (Use Slide </xsl:text>
									<xsl:value-of select="instructornotes/start"/>
									<xsl:text> to </xsl:text>
									<xsl:value-of select="instructornotes/stop"/>
									<xsl:text>)</xsl:text>

								</fo:block>
								
								<fo:block color="#0066CC" font-size="8pt" hyphenate="true" font-family="arial">
								<xsl:text>(</xsl:text>
								<xsl:value-of select="$assetURI"/>
								<xsl:value-of select="resourcefolder"/>
								<xsl:value-of select="nativeurl"/>
								<xsl:text>)</xsl:text>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>

		<xsl:if test="@type='video/youtube'">
			<fo:table table-layout="fixed"  margin="2mm 2mm 0mm 2mm">
				<fo:table-column column-width="10mm"/>
				<xsl:if test="../../type='suggestedstudy' or ../../type='assessment' or ../../type='homework' ">
					<fo:table-column column-width="75mm"/>
				</xsl:if>
				<xsl:if test="../../type!='suggestedstudy' and ../../type!='assessment' and ../../type!='homework' ">
					<fo:table-column column-width="50mm"/>
				</xsl:if>

				<fo:table-body border-left-width="0mm" border-right-width="0mm" border-top-width="0mm" border-bottom-width="0mm">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic height="10px" width="10px">
									<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/youtube.png)</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block hyphenate="true" font-family="arial" color="#535353" >
								<xsl:value-of select="label"/>
								<fo:block color="#0066CC"  font-size="8pt" font-family="arial" hyphenate="true">
								<xsl:text> (</xsl:text>
								<xsl:value-of select="nativeurl"/>
								<xsl:text>)</xsl:text>
								</fo:block>
							</fo:block>
							<xsl:if test="instructornotes/start!=''">
								<fo:block hyphenate="true" font-size="8pt" font-family="arial" color="#535353">
									<xsl:text>(Play From </xsl:text>
									<xsl:value-of select="instructornotes/start"/>
									<xsl:if test="instructornotes/stop!=''">
									<xsl:text> to </xsl:text>
									<xsl:value-of select="instructornotes/stop"/>
									</xsl:if>
									<xsl:text>)</xsl:text>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>

		<xsl:if test="@type='resource/url'">
			<fo:table table-layout="fixed"  margin="2mm 2mm 0mm 2mm">
				<fo:table-column column-width="10mm"/>
				<xsl:if test="../../type='suggestedstudy' or ../../type='assessment' or ../../type='homework' ">
					<fo:table-column column-width="75mm"/>
				</xsl:if>
				<xsl:if test="../../type!='suggestedstudy' and ../../type!='assessment' and ../../type!='homework' ">
					<fo:table-column column-width="50mm"/>
				</xsl:if>

				<fo:table-body border-left-width="0mm" border-right-width="0mm" border-top-width="0mm" border-bottom-width="0mm">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic height="10px" width="10px">
									<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/webresource.png)</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block hyphenate="true" font-family="arial" color="#535353">
									<xsl:value-of select="label"/>
									<fo:block color="#0066CC" font-size="8pt" font-family="arial">
									<xsl:text> (</xsl:text>
									<xsl:value-of select="nativeurl"/>
									<xsl:text>)</xsl:text>
									</fo:block>
							</fo:block>
							<xsl:if test="instructornotes/instruction!=''">
								<fo:block font-size="8pt" font-family="arial" color="#535353">
								<xsl:text>( </xsl:text>
									<xsl:value-of select="instructornotes/instruction"/>
								<xsl:text> )</xsl:text>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>

		<xsl:if test="@type='question'">
			<fo:table table-layout="fixed"  margin="2mm 2mm 0mm 2mm">
				<fo:table-column  column-width="10mm"/>

				<xsl:if test="../../type='suggestedstudy' or ../../type='assessment' or ../../type='homework' ">
					<fo:table-column column-width="75mm"/>
				</xsl:if>
				<xsl:if test="../../type!='suggestedstudy' and ../../type!='assessment' and ../../type!='homework' ">
					<fo:table-column column-width="50mm"/>
				</xsl:if>

				<fo:table-body border-left-width="0mm" border-right-width="0mm" border-top-width="0mm" border-bottom-width="0mm">
					<xsl:for-each select="question_set">
						<xsl:for-each select="question">
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<fo:external-graphic height="10px" width="10px">
											<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/questions.png)</xsl:attribute>
										</fo:external-graphic>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block hyphenate="true" wrap-option="wrap" font-size="8pt" font-family="arial" color="#535353">
									   
										<xsl:value-of select="question_text"/>
										<fo:block>
										<xsl:text>Options:</xsl:text>
										</fo:block>
										<xsl:if test="answers/option[1]">
										<fo:block hyphenate="true">
										<xsl:text>A. </xsl:text>
										<xsl:value-of select="answers/option[1]"/>
										</fo:block>
										</xsl:if>
										<xsl:if test="answers/option[2]">
										<fo:block hyphenate="true">
										<xsl:text>B. </xsl:text>
										<xsl:value-of select="answers/option[2]"/>
										</fo:block>
										</xsl:if>
										<xsl:if test="answers/option[3]">
										<fo:block hyphenate="true">
										<xsl:text>C. </xsl:text>
										<xsl:value-of select="answers/option[3]"/>
										</fo:block>
										</xsl:if>
										<xsl:if test="answers/option[4]">
										<fo:block hyphenate="true">
										<xsl:text>D. </xsl:text>
										<xsl:value-of select="answers/option[4]"/>
										</fo:block>
										</xsl:if>
										<xsl:if test="answers/option[5]">
										<fo:block hyphenate="true">
										<xsl:text>E. </xsl:text>
										<xsl:value-of select="answers/option[5]"/>
										</fo:block>
										</xsl:if>
										<xsl:if test="answers/option[6]">
										<fo:block hyphenate="true">
										<xsl:text>F. </xsl:text>
										<xsl:value-of select="answers/option[6]"/>
										</fo:block>
										</xsl:if>
										
										<xsl:text>Correct Opiton: </xsl:text>
										
										<xsl:if test="answers/@correct=0">
										<xsl:text> A</xsl:text>
										</xsl:if>
										<xsl:if test="answers/@correct=1">
										<xsl:text> B</xsl:text>
										</xsl:if>
										<xsl:if test="answers/@correct=2">
										<xsl:text> C</xsl:text>
										</xsl:if>
										<xsl:if test="answers/@correct=3">
										<xsl:text> D</xsl:text>
										</xsl:if>
										<xsl:if test="answers/@correct=4">
										<xsl:text> E</xsl:text>
										</xsl:if>
										<xsl:if test="answers/@correct=5">
										<xsl:text> F</xsl:text>
										</xsl:if>
										
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:for-each>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</xsl:if>

		<xsl:if test="@type='animation/swf'">
			<fo:table table-layout="fixed"  margin="2mm 2mm 0mm 2mm">
				<fo:table-column column-width="10mm"/>
				<xsl:if test="../../type='suggestedstudy' or ../../type='assessment' or ../../type='homework' ">
					<fo:table-column column-width="75mm"/>
				</xsl:if>
				<xsl:if test="../../type!='suggestedstudy' and ../../type!='assessment' and ../../type!='homework' ">
					<fo:table-column column-width="50mm"/>
				</xsl:if>

				<fo:table-body border-left-width="0mm" border-right-width="0mm" border-top-width="0mm" border-bottom-width="0mm">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic height="10px" width="10px">
									<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/swf.png)</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block hyphenate="true" color="#535353">
								<xsl:value-of select="label"/>
							</fo:block>
							<xsl:if test="instructornotes/instruction!=''">
								<fo:block font-size="8pt" font-family="arial" color="#535353">
									<xsl:text>( </xsl:text>
									<xsl:value-of select="instructornotes/instruction"/>
								<xsl:text> )</xsl:text>
								</fo:block>
								<fo:block color="#0066CC"  font-size="8pt" hyphenate="true" font-family="arial">
								<xsl:text>(</xsl:text>
								<xsl:value-of select="$assetURI"/>
								<xsl:value-of select="resourcefolder"/>
								<xsl:value-of select="nativeurl"/>
								<xsl:text>)</xsl:text>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>


		<xsl:if test="@type='animation/kmz'">
			<fo:table table-layout="fixed"  margin="2mm 2mm 0mm 2mm">
				<fo:table-column column-width="10mm"/>
				<xsl:if test="../../type='suggestedstudy' or ../../type='assessment' or ../../type='homework' ">
					<fo:table-column column-width="75mm"/>
				</xsl:if>
				<xsl:if test="../../type!='suggestedstudy' and ../../type!='assessment' and ../../type!='homework' ">
					<fo:table-column column-width="50mm"/>
				</xsl:if>

				<fo:table-body border-left-width="0mm" border-right-width="0mm" border-top-width="0mm" border-bottom-width="0mm">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic height="10px" width="10px">
									<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/KMZ.png)</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block hyphenate="true" color="#535353">
								<xsl:value-of select="label"/>
							</fo:block>
							<xsl:if test="instructornotes/instruction!=''">
								<fo:block font-size="8pt" font-family="arial" color="#535353">
									<xsl:text>( </xsl:text>
									<xsl:value-of select="instructornotes/instruction"/>
								<xsl:text> )</xsl:text>
								</fo:block>
								<fo:block color="#0066CC" font-family="arial" font-size="8pt" hyphenate="true">
								<xsl:text>(</xsl:text>
								<xsl:value-of select="$assetURI"/>
								<xsl:value-of select="resourcefolder"/>
								<xsl:value-of select="nativeurl"/>
								<xsl:text>)</xsl:text>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>

		<xsl:if test="@type='image/png'">
			<fo:table table-layout="fixed" margin="2mm 2mm 0mm 2mm">
				<fo:table-column column-width="10mm"/>
				<xsl:if test="../../type='suggestedstudy' or ../../type='assessment' or ../../type='homework' ">
					<fo:table-column column-width="75mm"/>
				</xsl:if>
				<xsl:if test="../../type!='suggestedstudy' and ../../type!='assessment' and ../../type!='homework' ">
					<fo:table-column column-width="50mm"/>
				</xsl:if>

				<fo:table-body border-left-width="0mm" border-right-width="0mm" border-top-width="0mm" border-bottom-width="0mm">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic height="10px" width="10px">
									<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/Image.png)</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block hyphenate="true" font-family="arial" color="#535353">
								<xsl:value-of select="label"/>
							</fo:block>
							<xsl:if test="instructornotes/instruction!=''">
								<fo:block font-size="8pt" font-family="arial" color="#535353">
									<xsl:text>( </xsl:text>
									<xsl:value-of select="instructornotes/instruction"/>
									<xsl:text> )</xsl:text>
								</fo:block>
								<fo:block color="#0066CC"  font-size="8pt" hyphenate="true" font-family="arial">
								<xsl:text>(</xsl:text>
								<xsl:value-of select="$assetURI"/>
								<xsl:value-of select="resourcefolder"/>
								<xsl:value-of select="nativeurl"/>
								<xsl:text>)</xsl:text>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>

		<xsl:if test="@type='handouts'">
			<fo:table table-layout="fixed"  margin="2mm 2mm 0mm 2mm">
				<fo:table-column column-width="10mm"/>
				<xsl:if test="../../type='suggestedstudy' or ../../type='assessment' or ../../type='homework' ">
					<fo:table-column column-width="75mm"/>
				</xsl:if>
				<xsl:if test="../../type!='suggestedstudy' and ../../type!='assessment' and ../../type!='homework' ">
					<fo:table-column column-width="50mm"/>
				</xsl:if>

				<fo:table-body border-left-width="0mm" border-right-width="0mm" border-top-width="0mm" border-bottom-width="0mm">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic height="10px" width="10px">
									<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/handOuts.png)</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block hyphenate="true" color="#535353">
								<xsl:value-of select="label"/>
							</fo:block>
							<xsl:if test="instructornotes/start!='' and instructornotes/stop!=''">
								<fo:block font-size="8pt" font-family="arial" color="#535353">
								    <xsl:text>  (Use Slide </xsl:text>
									<xsl:value-of select="instructornotes/start"/>
									<xsl:text> to </xsl:text>
									<xsl:value-of select="instructornotes/stop"/>
									<xsl:text>)</xsl:text>
								</fo:block>
								<fo:block color="#0066CC"  font-size="8pt" hyphenate="true" font-family="arial">
								<xsl:text>(</xsl:text>
								<xsl:value-of select="$assetURI"/>
								<xsl:value-of select="resourcefolder"/>
								<xsl:value-of select="nativeurl"/>
								<xsl:text>)</xsl:text>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>

		<xsl:if test="@type='textbook/scribd'">
			<fo:table table-layout="fixed"  margin="2mm 2mm 0mm 2mm">
				<fo:table-column column-width="10mm"/>
				<xsl:if test="../../type='suggestedstudy' or ../../type='assessment' or ../../type='homework' ">
					<fo:table-column column-width="75mm"/>
				</xsl:if>
				<xsl:if test="../../type!='suggestedstudy' and ../../type!='assessment' and ../../type!='homework' ">
					<fo:table-column column-width="50mm"/>
				</xsl:if>

				<fo:table-body border-left-width="0mm" border-right-width="0mm" border-top-width="0mm" border-bottom-width="0mm">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic height="10px" width="10px">
									<xsl:attribute name="src">url(<xsl:value-of select="$appPath"/>images/textBook.png)</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="9pt" hyphenate="true" color="#535353">
								<xsl:value-of select="label"/>
							</fo:block>
							<xsl:if test="instructornotes/start!=''">
								<fo:block font-size="8pt" font-family="arial" color="#535353">
									<xsl:text>(Start Page </xsl:text>
									<xsl:value-of select="instructornotes/start"/>
									<xsl:text> )</xsl:text>
								</fo:block>
								<fo:block font-size="8pt" hyphenate="true" font-family="arial">
								<xsl:text>(</xsl:text>
								<xsl:value-of select="$assetURI"/>
								<xsl:value-of select="resourcefolder"/>
								<xsl:value-of select="nativeurl"/>
								<xsl:text>)</xsl:text>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2008. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="file:///d:/share/newFormat.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth="" profilelength=""
		          urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="renderx" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal"
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
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no">
			<SourceSchema srcSchemaPath="..\..\..\share\newxml.xml" srcSchemaRoot="gooruclassplan" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/">
				<block path="fo:root/fo:layout-master-set/fo:simple-page-master/xsl:attribute/xsl:value-of" x="328" y="28"/>
				<block path="fo:root/fo:layout-master-set/fo:simple-page-master/xsl:attribute[1]/xsl:value-of" x="368" y="28"/>
				<block path="fo:root/fo:page-sequence/fo:flow/fo:table-and-caption[1]/fo:table/fo:table-body/xsl:for-each" x="288" y="28"/>
				<block path="fo:root/fo:page-sequence/fo:flow/fo:table-and-caption[1]/fo:table/fo:table-body/xsl:for-each/fo:table-row/fo:table-cell/fo:block/xsl:value-of" x="248" y="28"/>
				<block path="fo:root/fo:page-sequence/fo:flow/fo:table-and-caption[1]/fo:table/fo:table-body/xsl:for-each/fo:table-row/fo:table-cell[2]/fo:block/xsl:for-each" x="128" y="28"/>
				<block path="fo:root/fo:page-sequence/fo:flow/fo:table-and-caption[1]/fo:table/fo:table-body/xsl:for-each/fo:table-row/fo:table-cell[2]/fo:block/xsl:for-each/xsl:if/!=[0]" x="42" y="26"/>
				<block path="fo:root/fo:page-sequence/fo:flow/fo:table-and-caption[1]/fo:table/fo:table-body/xsl:for-each/fo:table-row/fo:table-cell[2]/fo:block/xsl:for-each/xsl:if" x="88" y="28"/>
				<block path="fo:root/fo:page-sequence/fo:flow/fo:table-and-caption[1]/fo:table/fo:table-body/xsl:for-each/fo:table-row/fo:table-cell[3]/xsl:if/!=[0]" x="162" y="26"/>
				<block path="fo:root/fo:page-sequence/fo:flow/fo:table-and-caption[1]/fo:table/fo:table-body/xsl:for-each/fo:table-row/fo:table-cell[3]/xsl:if" x="208" y="28"/>
				<block path="fo:root/fo:page-sequence/fo:flow/fo:table-and-caption[1]/fo:table/fo:table-body/xsl:for-each/fo:table-row/fo:table-cell[3]/xsl:if[1]/=[0]" x="122" y="26"/>
				<block path="fo:root/fo:page-sequence/fo:flow/fo:table-and-caption[1]/fo:table/fo:table-body/xsl:for-each/fo:table-row/fo:table-cell[3]/xsl:if[1]" x="168" y="28"/>
			</template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->