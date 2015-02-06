package org.ednovo.gooru.domain.cassandra.service;


import org.ednovo.gooru.cassandra.core.service.EntityCassandraService;
import org.ednovo.gooru.core.cassandra.model.OrganizationCio;
import org.ednovo.gooru.core.cassandra.model.ResourceCio;

public interface SchoolCassandraService extends EntityCassandraService<String, OrganizationCio> {

}