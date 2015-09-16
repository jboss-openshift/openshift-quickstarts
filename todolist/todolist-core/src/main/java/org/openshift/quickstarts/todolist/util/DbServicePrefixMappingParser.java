package org.openshift.quickstarts.todolist.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 */
public class DbServicePrefixMappingParser {


    public List<DbServicePrefixMapping> parseDbServicePrefixMappingEnvVar(String mappings) {
        List<DbServicePrefixMapping> list = new ArrayList<DbServicePrefixMapping>();
        StringTokenizer tokenizer = new StringTokenizer(mappings);
        while (tokenizer.hasMoreTokens()) {
            String mapping = tokenizer.nextToken();
            int idx = mapping.indexOf('=');
            if (idx == -1) {
                throw new IllegalArgumentException("Missing '=' in DB_SERVICE_PREFIX_MAPPING: " + mappings);
            }

            String prefix = mapping.substring(idx + 1);
            String nameAndDatabaseTypePair = mapping.substring(0, idx);

            int idx2 = nameAndDatabaseTypePair.lastIndexOf('-');
            if (idx2 == -1) {
                throw new IllegalArgumentException("Missing '-' in DB_SERVICE_PREFIX_MAPPING: " + mappings);
            }

            String databaseType = nameAndDatabaseTypePair.substring(idx2 + 1);
            String name = nameAndDatabaseTypePair.substring(0, idx2);

            list.add(new DbServicePrefixMapping(name, databaseType, prefix));
        }
        return list;
    }

    public static class DbServicePrefixMapping {
        private String name;
        private String databaseType;
        private String envPrefix;

        public DbServicePrefixMapping(String name, String databaseType, String envPrefix) {
            this.name = name;
            this.databaseType = databaseType;
            this.envPrefix = envPrefix;
        }

        public String getName() {
            return name;
        }

        public String getServiceName() {
            return (name.replace('-', '_') + "_" + databaseType).toUpperCase();
        }

        public String getDatabaseType() {
            return databaseType;
        }

        public String getEnvPrefix() {
            return envPrefix;
        }
    }
}
