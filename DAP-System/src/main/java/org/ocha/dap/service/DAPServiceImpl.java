package org.ocha.dap.service;

import java.util.List;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ocha.dap.dto.apiv2.DatasetV2DTO;
import org.ocha.dap.dto.apiv3.DatasetListV3DTO;
import org.ocha.dap.dto.apiv3.DatasetV3DTO.Resource;
import org.ocha.dap.dto.apiv3.DatasetV3WrapperDTO;
import org.ocha.dap.persistence.dao.CKANResourceDAO;
import org.ocha.dap.persistence.dao.UserDAO;
import org.ocha.dap.persistence.entity.CKANResource;
import org.ocha.dap.security.exception.AuthenticationException;
import org.ocha.dap.security.exception.InsufficientCredentialsException;
import org.ocha.dap.tools.GSONBuilderWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DAPServiceImpl implements DAPService {

	private static final Logger log = LoggerFactory.getLogger(DAPServiceImpl.class);

	private static String DATASET_LIST_V3_API_PATTERN = "http://%s/api/3/action/package_list";
	private static String DATASET_V3_API_PATTERN = "http://%s/api/3/action/package_show?id=";

	// can be useful to update a dataset
	private static String DATASET_V2_API_PATTERN = "http://%s/api/2/rest/dataset/";

	private final String urlBaseForDatasetsList;
	private final String urlBaseForDatasetContentV3;
	private final String urlBaseForDatasetContentV2;
	private final String technicalAPIKey;

	public DAPServiceImpl(final String host, final String technicalAPIKey) {
		super();
		this.urlBaseForDatasetsList = String.format(DATASET_LIST_V3_API_PATTERN, host);
		this.urlBaseForDatasetContentV3 = String.format(DATASET_V3_API_PATTERN, host);
		this.urlBaseForDatasetContentV2 = String.format(DATASET_V2_API_PATTERN, host);
		this.technicalAPIKey = technicalAPIKey;
	}

	@Autowired
	private UserDAO userDao;

	@Autowired
	private CKANResourceDAO resourceDAO;

	@Override
	public void checkForNewCKANResources() {
		final List<String> datasetList = getDatasetListDTOFromQuery(technicalAPIKey);
		for (final String datasetName : datasetList) {
			final DatasetV3WrapperDTO dataset = getDatasetDTOFromQueryV3(datasetName, technicalAPIKey);
			final List<Resource> resources = dataset.getResult().getResources();
			for (final Resource resource : resources) {
				if (resourceDAO.getCKANResource(resource.getId(), resource.getRevision_id()) == null) {
					resourceDAO.newCKANResourceDetected(resource.getId(), resource.getRevision_id(), resource.getRevision_timestamp(), dataset.getResult().getId(), dataset.getResult().getRevision_id(),
							dataset.getResult().getRevision_timestamp());
				}
			}
		}
	}
	
	@Override
	public List<CKANResource> listCKANResources() {
		return resourceDAO.listCKANResources();
	}

	@Override
	public List<String> getDatasetsListFromCKAN(final String userId) throws InsufficientCredentialsException {
		final String apiKey = userDao.getUserApiKey(userId);
		return getDatasetListDTOFromQuery(apiKey);
	}

	@Override
	public DatasetV3WrapperDTO getDatasetContentFromCKANV3(final String userId, final String datasetName) throws InsufficientCredentialsException {
		final String apiKey = userDao.getUserApiKey(userId);

		return getDatasetDTOFromQueryV3(datasetName, apiKey);

	}

	@Override
	public DatasetV2DTO getDatasetContentFromCKANV2(final String userId, final String datasetName) throws InsufficientCredentialsException {
		final String apiKey = userDao.getUserApiKey(userId);

		return getDatasetDTOFromQueryV2(datasetName, apiKey);
	}

	@Override
	public void updateDatasetContent(final String userId, final String datasetName, final DatasetV2DTO datasetV2DTO) throws InsufficientCredentialsException {
		final String apiKey = userDao.getUserApiKey(userId);

		updateDatasetDTO(datasetName, apiKey, datasetV2DTO);
	}

	List<String> getDatasetListDTOFromQuery(final String apiKey) {
		final String jsonResult = performHttpGET(urlBaseForDatasetsList, apiKey);
		if (jsonResult == null) {
			return null;
		} else {
			final DatasetListV3DTO returnedValue = GSONBuilderWrapper.getGSON().fromJson(jsonResult, DatasetListV3DTO.class);
			return returnedValue.getResult();
		}
	}

	@Override
	public boolean authenticate(final String id, final String password) throws AuthenticationException {
		return userDao.authenticate(id, password);
	}

	DatasetV3WrapperDTO getDatasetDTOFromQueryV3(final String datasetName, final String apiKey) {
		final String urlForDataSet = String.format("%s%s", urlBaseForDatasetContentV3, datasetName);
		final String jsonResult = performHttpGET(urlForDataSet, apiKey);
		if (jsonResult == null) {
			return null;
		} else {

			return GSONBuilderWrapper.getGSON().fromJson(jsonResult, DatasetV3WrapperDTO.class);
		}
	}

	DatasetV2DTO getDatasetDTOFromQueryV2(final String datasetName, final String apiKey) {
		final String urlForDataSet = String.format("%s%s", urlBaseForDatasetContentV2, datasetName);
		final String jsonResult = performHttpGET(urlForDataSet, apiKey);
		if (jsonResult == null) {
			return null;
		} else {

			return GSONBuilderWrapper.getGSON().fromJson(jsonResult, DatasetV2DTO.class);
		}
	}

	void updateDatasetDTO(final String datasetName, final String apiKey, final DatasetV2DTO datasetV2DTO) {
		final String urlForDataSet = String.format("%s%s", urlBaseForDatasetContentV2, datasetName);

		final String query = GSONBuilderWrapper.getGSON().toJson(datasetV2DTO);

		final String jsonResult = performHttpPOST(urlForDataSet, apiKey, query);
		jsonResult.toString();
	}

	private String performHttpGET(final String url, final String apiKey) {
		String responseBody = null;
		final DefaultHttpClient httpclient = new DefaultHttpClient();

		final HttpGet httpGet = new HttpGet(url);
		try {
			httpGet.addHeader("Content-Type", "application/json");
			httpGet.addHeader("accept", "application/json");

			if (apiKey != null) {
				httpGet.addHeader("X-CKAN-API-Key", apiKey);
			}

			final ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpGet, responseHandler);
		} catch (final Exception e) {
			log.debug(e.toString(), e);
		}

		return responseBody;

	}

	private String performHttpPOST(final String url, final String apiKey, final String query) {
		String responseBody = null;
		final DefaultHttpClient httpclient = new DefaultHttpClient();

		final HttpPost httpPost = new HttpPost(url);
		try {

			final StringEntity se = new StringEntity(query);
			httpPost.setEntity(se);

			// se.setContentType("text/xml");
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("accept", "application/json");

			if (apiKey != null) {
				httpPost.addHeader("X-CKAN-API-Key", apiKey);
			}

			// log.debug("about to send query: " + query);

			final ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpPost, responseHandler);
		} catch (final Exception e) {
			log.debug(e.toString(), e);
		}

		return responseBody;

	}

}
