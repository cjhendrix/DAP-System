package org.ocha.dap.persistence.dao.ckan;

import java.util.Date;
import java.util.List;

import org.ocha.dap.model.validation.ValidationReport;
import org.ocha.dap.persistence.entity.ckan.CKANDataset;
import org.ocha.dap.persistence.entity.ckan.CKANResource;

public interface CKANResourceDAO {
	public void newCKANResourceDetected(final String id, final String revision_id, final String name, final Date revision_timestamp, final String parentDataset_name, final String parentDataset_id,
			final String parentDataset_revision_id, final Date parentDataset_revision_timestamp);

	/**
	 * Flags the given record as Downloaded
	 * 
	 * @param id
	 * @param revision_id
	 */
	public void flagCKANResourceAsDownloaded(final String id, final String revision_id);

	/**
	 * Flags the given record as Tech Evaluation Success and stores which evaluator triggered the success
	 * 
	 * @param id
	 * @param revision_id
	 */
	public void flagCKANResourceAsTechEvaluationSuccess(final String id, final String revision_id, final ValidationReport report);

	/**
	 * Flags the given record as Tech Evaluation Fail and stores which evaluator triggered the failure
	 * 
	 * @param id
	 * @param revision_id
	 */
	public void flagCKANResourceAsTechEvaluationFail(final String id, final String revision_id, final ValidationReport report);

	/**
	 * 
	 * @param id
	 * @param revision_id
	 * @param evaluator
	 */
	public void flagCKANResourceAsImportSuccess(final String id, final String revision_id, final CKANDataset.Type importer);

	public void flagCKANResourceAsImportFail(final String id, final String revision_id, final CKANDataset.Type importer);

	/**
	 * Flags the given record as Outdated
	 * 
	 * @param id
	 * @param revision_id
	 */
	public void flagCKANResourceAsOutdated(final String id, final String revision_id);

	public CKANResource getCKANResource(final String id, final String revision_id);

	public List<CKANResource> listCKANResourceRevisions(final String id);

	public List<CKANResource> listCKANResources();

	public void deleteAllCKANResourcesRecords();

}
