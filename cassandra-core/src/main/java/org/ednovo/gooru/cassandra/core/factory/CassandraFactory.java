package org.ednovo.gooru.cassandra.core.factory;

@SuppressWarnings("rawtypes")
public abstract class CassandraFactory<T extends CassandraFactory> implements CassandraKeyspace {

	private T factory;
	
	public T getCassandraFactory(){
		return factory;
	}
	
	public void setCassandrafactory (T factory){
		this.factory = factory;
	}

}
