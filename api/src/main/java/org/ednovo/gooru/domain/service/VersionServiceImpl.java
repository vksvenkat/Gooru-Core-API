package org.ednovo.gooru.domain.service;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VersionServiceImpl extends BaseServiceImpl implements VersionService {
	
	@Autowired
	private SettingService settingService;
	
	@Override
	public	String getDefaultVersion(){
		
	return settingService.getConfigSetting(ConfigConstants.SERVICE_DEFAULT_VERSION, 0, TaxonomyUtil.GOORU_ORG_UID);
	}

}
