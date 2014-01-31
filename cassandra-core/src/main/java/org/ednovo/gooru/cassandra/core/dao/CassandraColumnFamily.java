/**
 * 
 */
package org.ednovo.gooru.cassandra.core.dao;

import org.ednovo.gooru.core.cassandra.model.ReverseIndexColumnSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;

/**
 * @author SearchTeam
 * 
 */
public class CassandraColumnFamily {

	protected static final Logger LOG = LoggerFactory.getLogger(CassandraColumnFamily.class);

	private ColumnFamily<String, String> columnFamily = null;

	private ColumnFamily<String, String> riColumnFamily = null;

	private String columnFamilyName;
	
	private ReverseIndexColumnSetting riColumnSettings;

	public CassandraColumnFamily() {
		
	}
	
	public CassandraColumnFamily(String columnFamilyName) {
		this(columnFamilyName, null);
	}
	
	public CassandraColumnFamily(ReverseIndexColumnSetting riColumnSettings) {
		setRiColumnSettings(riColumnSettings);
	}

	public CassandraColumnFamily(final String columnFamilyName,
			ReverseIndexColumnSetting riColumnSettings) {
		setColumnFamilyName(columnFamilyName);
		setRiColumnSettings(riColumnSettings);
	}
	
	public void init(Keyspace keyspace) {
		if (keyspace != null) {
			columnFamily = createCF(keyspace, getColumnFamilyName());
		} else {
			getLog().error("Cassandra Mapper for " + getColumnFamilyName() + " : FAILED");
		}
		if (getRiColumnSettings() != null && getRiColumnSettings().size() > 0 && columnFamily != null) {
			riColumnFamily = createCF(keyspace, getColumnFamilyName() + "_ri");
		}
	}
	
	private ColumnFamily<String, String> createCF(Keyspace keyspace, String cfName) {
		ColumnFamily<String, String> columnFamily = null;
		try {
			columnFamily = new ColumnFamily<String, String>(cfName, StringSerializer.get(), StringSerializer.get());
			try {
				keyspace.getColumnFamilyProperties(cfName);
			} catch (Exception ex) {
				String type = "UTF8Type";
				keyspace.createColumnFamily(columnFamily, ImmutableMap.<String, Object> builder().put("key_validation_class", type).put("comparator", type).build());
				LOG.warn("Cassandra ColumnFamily : " + cfName + " doesn't exist, Created!");
			}
		} catch (Exception e) {
			LOG.error("Could not create column family : " + cfName + " : " + e.getMessage());
		}
		return columnFamily;
	}

	public ColumnFamily<String, String> getColumnFamily() {
		return columnFamily;
	}

	public ColumnFamily<String, String> getRiColumnFamily() {
		return riColumnFamily;
	}

	public ReverseIndexColumnSetting getRiColumnSettings() {
		return riColumnSettings;
	}

	public void setRiColumnSettings(ReverseIndexColumnSetting riColumnSettings) {
		this.riColumnSettings = riColumnSettings;
	}

	public String getColumnFamilyName() {
		return columnFamilyName;
	}

	public void setColumnFamilyName(String columnFamilyName) {
		this.columnFamilyName = columnFamilyName;
	}

	public static Logger getLog() {
		return LOG;
	}

}
