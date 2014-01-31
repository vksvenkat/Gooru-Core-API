/**
 * 
 */
package org.ednovo.gooru.core.api.model;

/**
 * An entity indexable by Gooru Indexers. Models should implement this to be
 * usable in the indexing process.
 */
public interface IndexableEntry {
	String getEntryId();
}
