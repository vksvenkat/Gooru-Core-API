package org.ednovo.gooru.core.application.util.formatter;

import java.util.Map;

import org.ednovo.gooru.core.api.model.Thumbnail;

public class ResourceFo {

	private String resourceInstanceId;
	private String resourcefolder;
	private String label;
	private String typeDesc;
	private String type;
	private String id;
	private String sharing;
	private String thumbnail;
	private String assetURI;
	private String nativeurl;
	private String description;
	private InstructionNotesFo instructionnotes;
	private ResourceStatusFo resourcestatus;
	private ResourceInfoFo resourceInfo;
	private String category;
	private Thumbnail thumbnails;
	private String recordSource;
	
	private Map<String, String> customFieldValues;

	public ResourceFo() {
		instructionnotes = new InstructionNotesFo();
		resourcestatus = new ResourceStatusFo();
		resourceInfo = new ResourceInfoFo();
	}

	public String getAssetURI() {
		return assetURI;
	}

	public void setAssetURI(String assetURI) {
		this.assetURI = assetURI;
	}

	public String getResourceInstanceId() {
		return resourceInstanceId;
	}

	public void setResourceInstanceId(String resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

	public String getResourcefolder() {
		return resourcefolder;
	}

	public void setResourcefolder(String resourcefolder) {
		this.resourcefolder = resourcefolder;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSharing() {
		return sharing;
	}

	public void setSharing(String sharing) {
		this.sharing = sharing;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getNativeurl() {
		return nativeurl;
	}

	public void setNativeurl(String nativeurl) {
		this.nativeurl = nativeurl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public InstructionNotesFo getInstructionnotes() {
		return instructionnotes;
	}

	public void setInstructionnotes(InstructionNotesFo instructionnotes) {
		this.instructionnotes = instructionnotes;
	}

	public ResourceStatusFo getResourcestatus() {
		return resourcestatus;
	}

	public void setResourcestatus(ResourceStatusFo resourcestatus) {
		this.resourcestatus = resourcestatus;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setThumbnails(Thumbnail thumbnails) {
		this.thumbnails = thumbnails;
	}

	public Thumbnail getThumbnails() {
		return thumbnails;
	}

	public ResourceInfoFo getResourceInfo() {
		return resourceInfo;
	}

	public void setResourceInfo(ResourceInfoFo resourceInfoFo) {
		this.resourceInfo = resourceInfoFo;
	}


	public Map<String, String> getCustomFieldValues() {
		return customFieldValues;
	}

	public void setCustomFieldValues(Map<String, String> customFieldValues) {
		this.customFieldValues = customFieldValues;
	}

	public void setRecordSource(String recordSource) {
		this.recordSource = recordSource;
	}

	public String getRecordSource() {
		return recordSource;
	}


	public class ResourceStatusFo {

		private String statusIsFrameBreaker;
		private String statusIsBroken;

		public String getStatusIsFrameBreaker() {
			return statusIsFrameBreaker;
		}

		public void setStatusIsFrameBreaker(String statusIsFrameBreaker) {
			this.statusIsFrameBreaker = statusIsFrameBreaker;
		}

		public String getStatusIsBroken() {
			return statusIsBroken;
		}

		public void setStatusIsBroken(String statusIsBroken) {
			this.statusIsBroken = statusIsBroken;
		}
	}

	public class ResourceInfoFo {

		private Integer numOfPages;

		public Integer getNumOfPages() {
			return numOfPages;
		}

		public void setNumOfPages(Integer numOfPages) {
			this.numOfPages = numOfPages;
		}

	}

	public class InstructionNotesFo {

		private String start;
		private String stop;
		private String instruction;

		public String getStart() {
			return start;
		}

		public void setStart(String start) {
			this.start = start;
		}

		public String getStop() {
			return stop;
		}

		public void setStop(String stop) {
			this.stop = stop;
		}

		public String getInstruction() {
			return instruction;
		}

		public void setInstruction(String instruction) {
			this.instruction = instruction;
		}

	}

	public class ResourceSourceFo {

		private String resourcesourceid;

		private String resourcesourceurl;

		private String resourcedomainname;

		public String getResourcesourceid() {
			return resourcesourceid;
		}

		public void setResourcesourceid(String resourcesourceid) {
			this.resourcesourceid = resourcesourceid;
		}

		public String getResourcesourceurl() {
			return resourcesourceurl;
		}

		public void setResourcesourceurl(String resourcesourceurl) {
			this.resourcesourceurl = resourcesourceurl;
		}

		public String getResourcedomainname() {
			return resourcedomainname;
		}

		public void setResourcedomainname(String resourcedomainname) {
			this.resourcedomainname = resourcedomainname;
		}
		
		

	}
}
