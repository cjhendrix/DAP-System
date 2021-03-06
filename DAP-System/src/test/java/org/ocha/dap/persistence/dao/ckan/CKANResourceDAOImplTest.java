package org.ocha.dap.persistence.dao.ckan;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocha.dap.persistence.entity.ckan.CKANResource;
import org.ocha.dap.persistence.entity.ckan.CKANResource.WorkflowState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/ctx-config-test.xml", "classpath:/ctx-core.xml", "classpath:/ctx-dao.xml", "classpath:/ctx-persistence-test.xml" })
public class CKANResourceDAOImplTest {

	@Autowired
	private CKANResourceDAO ckanResourceDAO;

	@After
	public void tearDown() {
		ckanResourceDAO.deleteAllCKANResourcesRecords();
	}

	@Test
	public void testStandardWorkflow() {
		Assert.assertEquals(0, ckanResourceDAO.listCKANResources().size());

		final Date revisionTs = new Date();
		ckanResourceDAO.newCKANResourceDetected("newUnitTestResourceId", "newUnitTestResourceRevId", "newUnitTestResourceName", revisionTs, "theParent", "parentDataset_id",
				"parentDataset_revision_id", revisionTs);

		Assert.assertEquals(1, ckanResourceDAO.listCKANResources().size());

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
			Assert.assertEquals(WorkflowState.DETECTED_NEW, r.getWorkflowState());
			Assert.assertEquals(revisionTs, r.getRevision_timestamp());
			Assert.assertNull(r.getDownloadDate());
		}

		ckanResourceDAO.flagCKANResourceAsDownloaded("newUnitTestResourceId", "newUnitTestResourceRevId");

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
			Assert.assertEquals(WorkflowState.DOWNLOADED, r.getWorkflowState());
			Assert.assertEquals(revisionTs, r.getRevision_timestamp());
			Assert.assertNotNull(r.getDownloadDate());
		}
		Assert.assertEquals(1, ckanResourceDAO.listCKANResources().size());

		final Date revision2Ts = new Date();
		ckanResourceDAO.newCKANResourceDetected("newUnitTestResourceId", "newUnitTestResourceRevId2", "newUnitTestResourceName2", revision2Ts, "theParent", "parentDataset_id",
				"parentDataset_revision_id", revision2Ts);

		// no change expected, the resource already exist
		Assert.assertEquals(2, ckanResourceDAO.listCKANResources().size());

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
			Assert.assertEquals(WorkflowState.DOWNLOADED, r.getWorkflowState());
			Assert.assertEquals(revisionTs, r.getRevision_timestamp());
			Assert.assertNotNull(r.getDownloadDate());
		}

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId2");
			Assert.assertEquals(WorkflowState.DETECTED_REVISION, r.getWorkflowState());
			Assert.assertEquals(revision2Ts, r.getRevision_timestamp());
			Assert.assertNull(r.getDownloadDate());
		}
	}

}
