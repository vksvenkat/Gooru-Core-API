package org.ednovo.gooru.cassandra.core.factory;

public abstract class CassandraFactory<T extends CassandraFactory> implements CassandraKeyspace {

	T factory;
	
	public T getCassandraFactory(){
		return factory;
	}
	
	public void setCassandrafactory (T factory){
		this.factory = factory;
	}

}
