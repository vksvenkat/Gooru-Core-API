package org.ednovo.gooru.domain.cassandra.service;

import java.util.Collection;

import com.netflix.astyanax.model.Rows;


public interface LogDashCassandraService  {

	Rows<String, String> readWithKeyListColumnList(Collection<String> ids,Collection<String> field, int i);

}
