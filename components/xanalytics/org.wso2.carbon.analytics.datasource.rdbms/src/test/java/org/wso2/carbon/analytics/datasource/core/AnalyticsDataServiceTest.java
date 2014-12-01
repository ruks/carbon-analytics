/*
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.analytics.datasource.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.wso2.carbon.analytics.dataservice.AnalyticsDataService;
import org.wso2.carbon.analytics.dataservice.AnalyticsDataServiceImpl;
import org.wso2.carbon.base.MultitenantConstants;

/**
 * This class represents the analytics data service tests.
 */
public class AnalyticsDataServiceTest {

    private AnalyticsDataService service;
    
    @BeforeSuite
    public void setup() throws NamingException, AnalyticsException, IOException {
        AnalyticsDataSource ads = H2FileDBAnalyticsDataSourceTest.cleanupAndCreateDS();
        this.service = new AnalyticsDataServiceImpl(ads);
    }
    
    @Test
    public void testIndexAddRetrieve() throws AnalyticsException {
        this.indexAddRetrieve(MultitenantConstants.SUPER_TENANT_ID);
        this.indexAddRetrieve(1);
        this.indexAddRetrieve(15001);
    }
    
    private void indexAddRetrieve(int tenantId) throws AnalyticsException {
        this.service.clearIndices(tenantId, "T1");
        Set<String> columns = new HashSet<String>();
        columns.add("C1");
        columns.add("C2");
        columns.add("C3");
        service.setIndices(tenantId, "T1", columns);
        Set<String> columnsIn = service.getIndices(tenantId, "T1");
        Assert.assertEquals(columnsIn, columns);
        this.service.clearIndices(tenantId, "T1");
    }

    private Set<Record> recordGroupsToSet(RecordGroup[] rgs) throws AnalyticsException {
        Set<Record> result = new HashSet<Record>();
        for (RecordGroup rg : rgs) {
            result.addAll(rg.getRecords());
        }
        return result;
    }
    
    private List<Record> generateRecords(int tenantId, String tableName, int i, int c, long time, int timeOffset) {
        List<Record> result = new ArrayList<Record>();
        Map<String, Object> values;
        long timeTmp;
        for (int j = 0; j < c; j++) {
            values = new HashMap<String, Object>();
            values.put("server_name", "ESB-" + i);
            values.put("ip", "192.168.0." + (i % 256));
            values.put("tenant", i);
            values.put("spam_index", i + 0.3454452);
            values.put("important", i % 2 == 0 ? true : false);
            values.put("sequence", i + 104050000L);
            values.put("summary", "Joey asks, how you doing?");
            values.put("log", "Exception in Sequence[" + i + "," + j + "]");
            if (time != -1) {
                timeTmp = time;
                time += timeOffset;
            } else {
                timeTmp = System.currentTimeMillis();
            }
            result.add(new Record(tenantId, tableName, values, timeTmp));
        }
        return result;
    }
    
    private void cleanupTable(int tenantId, String tableName) throws AnalyticsException {
        if (this.service.tableExists(tenantId, tableName)) {
            this.service.deleteTable(tenantId, tableName);
        }
    }
    
    @Test
    public void testDataRecordAddReadPerformance() throws AnalyticsException {
        this.cleanupTable(50, "TableX");
        System.out.println("\n************** START ANALYTICS DS (WITHOUT INDEXING, H2-FILE) PERF TEST **************");
        int n = 55, batch = 1000;
        List<Record> records;
        
        /* warm-up */
        this.service.createTable(50, "TableX");      
        for (int i = 0; i < 10; i++) {
            records = this.generateRecords(50, "TableX", i, batch, -1, -1);
            this.service.put(records);
        }
        this.cleanupTable(50, "TableX");
        
        this.service.createTable(50, "TableX");        
        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            records = this.generateRecords(50, "TableX", i, batch, -1, -1);
            this.service.put(records);
        }
        long end = System.currentTimeMillis();
        System.out.println("* Records: " + (n * batch));
        System.out.println("* Write Time: " + (end - start) + " ms.");
        System.out.println("* Write Throughput (TPS): " + (n * batch) / (double) (end - start) * 1000.0);
        Set<Record> recordsIn = this.recordGroupsToSet(this.service.get(50, "TableX", null, -1, -1, 0, -1));
        Assert.assertEquals(recordsIn.size(), (n * batch));
        end = System.currentTimeMillis();
        System.out.println("* Read Time: " + (end - start) + " ms.");
        System.out.println("* Read Throughput (TPS): " + (n * batch) / (double) (end - start) * 1000.0);
        this.cleanupTable(50, "TableX");
        System.out.println("\n************** END ANALYTICS DS (WITHOUT INDEXING, H2-FILE) PERF TEST **************");
    }
        
}
