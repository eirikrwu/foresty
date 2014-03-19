package com.foresty.repository;

import com.foresty.DomainConfig;
import com.foresty.model.Log;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * Created by EveningSun on 14-3-18.
 */
@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DomainConfig.class)
public class LogRepositoryUnitTest {
    @Autowired
    private LogRepository logRepository;

    @Before
    public void setUp() {
        this.logRepository.deleteAll();
    }

    @After
    public void tearDown() {
        this.logRepository.deleteAll();
    }

    @Test
    public void testFindChildNodes_AtDepth0() throws Exception {
        addLog("node1", null);
        addLog("node2", null);
        addLog("node2", "node3");

        List<String> childNodes = this.logRepository.getChildNodes(0, null);
        Assert.assertEquals("Should find 2 child node names.", 2, childNodes.size());
    }

    @Test
    public void testFindChildNodes_AtOtherDepth() throws Exception {
        addLog("node1", "node3");
        addLog("node1", "node4");
        addLog("node1", "node3");
        addLog("node2", "node5");

        List<String> childNodes = this.logRepository.getChildNodes(1, "node1");
        Assert.assertEquals("Should find 2 child node names.", 2, childNodes.size());
    }

    @Test
    public void testFindLogs_LogAtDepth0() throws Exception {
        addLog(null, null);
        addLog(null, null);
        addLog("node1", null);

        List<Log> logs = this.logRepository.getLogs(0, null);
        Assert.assertEquals("Should find 2 log message.", 2, logs.size());
    }

    @Test
    public void testFindLogs_LogAtOtherDepth() throws Exception {
        addLog("node1", null);
        addLog("node1", null);
        addLog("node1", "node2");

        List<Log> logs = this.logRepository.getLogs(1, "node1");
        Assert.assertEquals("Should find 2 log message.", 2, logs.size());
    }

    @Test
    public void testFindLogs_LogAtMaxDepth() throws Exception {
        addMaxDepthLog("node");
        addMaxDepthLog("node");
        addLog("node1", "node2");

        List<Log> logs = this.logRepository.getLogs(Log.MAX_DEPTH, "node" + Log.MAX_DEPTH);
        Assert.assertEquals("Should find 2 log message.", 2, logs.size());
    }

    private Log addLog(String node1, String node2) {
        Log log = new Log();
        log.setTimestamp(new Date());
        log.setMessage("some message");
        log.setNode1(node1);
        log.setNode2(node2);

        return this.logRepository.save(log);
    }

    private Log addMaxDepthLog(String nodePrefix) {
        Log log = new Log();
        log.setTimestamp(new Date());
        log.setMessage("some message");

        List<String> path = Lists.newArrayList();
        for (int i = 0; i < Log.MAX_DEPTH; i++) {
            path.add(nodePrefix + (i + 1));
        }
        log.setPath(path);

        return this.logRepository.save(log);
    }
}
