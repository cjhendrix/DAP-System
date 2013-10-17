package org.ocha.dap.service;

import java.io.IOException;
import java.util.List;

import org.ocha.dap.dto.apiv2.DatasetV2DTO;
import org.ocha.dap.dto.apiv3.DatasetV3WrapperDTO;
import org.ocha.dap.persistence.entity.CKANResource;
import org.ocha.dap.persistence.entity.CKANResource.WorkflowState;
import org.ocha.dap.security.exception.AuthenticationException;
import org.ocha.dap.security.exception.InsufficientCredentialsException;

public interface DAPService {

	/**
	 * will try to get all the resources from CKAN instance first datasets and
	 * then resources
	 * 
	 * Il some resources are new, will register a CKANResource in DAP db with
	 * {@link WorkflowState#Detected}
	 */
	public void checkForNewCKANResources();

	public List<CKANResource> listCKANResources();

	/**
	 * 
	 * downloads the file associated to the given id / revision
	 * and flags the record as {@link WorkflowState#DOWNLOADED}
	 * 
	 * The record must be in a Workflow State allowing download  
	 */
	public void downloadFileForCKANResource(final String id, final String revision_id) throws IOException;
	
	/**
	 * 
	 * evaluate the file associated to the given id / revision
	 * and flags the record as {@link WorkflowState#TECH_EVALUTATION_SUCCESS} or {@link WorkflowState#TECH_EVALUTATION_FAIL}
	 * 
	 * The record must be in a Workflow State allowing download  
	 */
	public void evaluateFileForCKANResource(final String id, final String revision_id) throws IOException;

	/**
	 * return the list of the Datasets Performing the query on behalf of the
	 * user
	 * 
	 * @param userId
	 *            id of the user performing the query
	 */
	public List<String> getDatasetsListFromCKAN(final String userId) throws InsufficientCredentialsException;

	/**
	 * uses the CKAN api V3 fetchs the content of the given dataset Performing
	 * the query on behalf of the user
	 * 
	 * @param userId
	 *            id of the user performing the query
	 * @param datasetName
	 *            name of the dataset
	 */
	public DatasetV3WrapperDTO getDatasetContentFromCKANV3(final String userId, final String datasetName) throws InsufficientCredentialsException;

	public DatasetV2DTO getDatasetDTOFromQueryV2(final String datasetName, final String apiKey);
	public DatasetV3WrapperDTO getDatasetDTOFromQueryV3(final String datasetName, final String apiKey);
	/**
	 * uses the CKAN api V2 fetchs the content of the given dataset Performing
	 * the query on behalf of the user
	 * 
	 * @param userId
	 *            id of the user performing the query
	 * @param datasetName
	 *            name of the dataset
	 */
	public DatasetV2DTO getDatasetContentFromCKANV2(final String userId, final String datasetName) throws InsufficientCredentialsException;

	public void updateDatasetContent(final String userId, final String datasetName, final DatasetV2DTO datasetV2DTO) throws InsufficientCredentialsException;

	public boolean authenticate(final String id, final String password) throws AuthenticationException;

}
