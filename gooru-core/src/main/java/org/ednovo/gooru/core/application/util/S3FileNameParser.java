package org.ednovo.gooru.core.application.util;

public class S3FileNameParser {

	public static enum S3PATHS {
		DE_0("DE.0", "Assessment items/"), 
		TF_02("TF.02", "Assessment items/TF Assessment Images/TF.02/"),
		TF_03("TF.03", "Assessment items/TF Assessment Images/TF.03(precalc-trig)/"),
		ST_01("ST.01", "Assessment items/ST Assessments/"),
		RX_01("RX.01", "Assessment items/RX/"),
		IND("IND.", "Assessment items/IND Assessment Images/"),
		GA("GA.","Assessment items/GA Assessment Images/"),
		DE("DE.","Assessment items/DE Assessment Images/"),
		CA_0("CA.0","Assessment items/CA Assessment Images/"),
		CA_16("CA.16","Assessment items/CA Assessment Images/CA.16/"),
		CA_17("CA.17","Assessment items/CA Assessment Images/CA.17/"),		
		AZ("AZ.","Assessment items/AZ Assessment Images/"),
		AR_05("AR.05","Assessment items/AR Assessment Images/AR.05/"),
		AR_06("AR.06","Assessment items/AR Assessment Images/AR.06/"),
		AR_07("AR.07","Assessment items/AR Assessment Images/AR.07/"),
		AR_08("AR.08","Assessment items/AR Assessment Images/AR.08/"),
		AR_09("AR.09","Assessment items/AR Assessment Images/AR.09/"),
		AP_01("AP.01","Assessment items/AP Assessment Images/AP.01/"),
		AP_02("AP.02","Assessment items/AP Assessment Images/"),
		AL_01("AL.01","Assessment items/AL Assessment Images/AL.1/"),
		AK_01("AK.01","Assessment items/AK Assessment Images/AK.01/"),
		AK_02("AK.02","Assessment items/AK Assessment Images/AK.02/"),
		AK_03("AK.03","Assessment items/AK Assessment Images/AK.03/"),
		AK_04("AK.04","Assessment items/AK Assessment Images/AK.04/"),
		AK_05("AK.05","Assessment items/AK Assessment Images/AK.05/"),
		AK_06("AK.06","Assessment items/AK Assessment Images/AK.06/"),
		AK_07("AK.07","Assessment items/AK Assessment Images/Ak.07/"),
		AK_08("AK.08","Assessment items/AK Assessment Images/AK.08/"),
		AK_09("AK.09","Assessment items/AK Assessment Images/AK.09/"),
		AK_10("AK.10","Assessment items/AK Assessment Images/AK.10/"),
		AK_11("AK.11","Assessment items/AK Assessment Images/AK.11/"),
		AK_12("AK.12","Assessment items/AK Assessment Images/AK.12/"),
		AK_13("AK.13","Assessment items/AK Assessment Images/AK.13/"),
		TF_01("TF.01", "Assessment items/TF Assessment Images/TF.01/");

		private String name;
		private String path;

		S3PATHS(String name, String path) {
			this.name = name;
			this.path = path;
		}

		public String getName() {
			return name;
		}

		public String getPath() {
			return path;
		}
	}

	public static String getFilePath(String fileName) {
		if (fileName != null && fileName.length() > 3) {
			for (S3PATHS type : S3PATHS.values()) {
				if (fileName.trim().startsWith(type.getName())) {
					return type.getPath();
				}
			}
		}
		return "";
	}

}
