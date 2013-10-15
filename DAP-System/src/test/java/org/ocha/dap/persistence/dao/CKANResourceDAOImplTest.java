package org.ocha.dap.persistence.dao;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocha.dap.persistence.entity.CKANResource;
import org.ocha.dap.persistence.entity.CKANResource.WorkflowState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/ctx-config-test.xml", "classpath:/ctx-core.xml", "classpath:/ctx-dao.xml",
		"classpath:/ctx-persistence-test.xml" })
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
		ckanResourceDAO.newCKANResourceDetected("newUnitTestResourceId", "newUnitTestResourceRevId", revisionTs, "parentDataset_id",
				"parentDataset_revision_id", revisionTs);

		Assert.assertEquals(1, ckanResourceDAO.listCKANResources().size());

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
			Assert.assertEquals(WorkflowState.Detected, r.getWorkfowState());
			Assert.assertEquals(revisionTs, r.getRevision_timestamp());
			Assert.assertNull(r.getDownloadDate());
		}

		ckanResourceDAO.flagCKANResourceAsDownloaded("newUnitTestResourceId", "newUnitTestResourceRevId");

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
			Assert.assertEquals(WorkflowState.Downloaded, r.getWorkfowState());
			Assert.assertEquals(revisionTs, r.getRevision_timestamp());
			Assert.assertNotNull(r.getDownloadDate());
		}
		Assert.assertEquals(1, ckanResourceDAO.listCKANResources().size());
	}

}