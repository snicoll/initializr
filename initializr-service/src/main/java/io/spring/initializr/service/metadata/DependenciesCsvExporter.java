/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.service.metadata;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

/**
 *
 * @author Stephane Nicoll
 */
@Component
public class DependenciesCsvExporter {

	private static final Logger logger = LoggerFactory.getLogger(DependenciesCsvExporter.class);

	private static final String QUERY_TEMPLATE = "{\n"
			+ "  \"size\" : 0,\n"
			+ "  \"query\" : {\n"
			+ "    \"bool\" : {\n"
			+ "      \"filter\" : [ {\n"
			+ "        \"range\" : {\n"
			+ "          \"generationTimestamp\" : {\n"
			+ "            \"from\" : %d,\n"
			+ "            \"to\" : %d,\n"
			+ "            \"include_lower\" : true,\n"
			+ "            \"include_upper\" : true\n"
			+ "          }\n"
			+ "        }\n"
			+ "      }, {\n"
			+ "        \"bool\" : {\n"
			+ "          \"must\" : {\n"
			+ "            \"terms\" : {\n"
			+ "              \"dependencies\" : [ \"%s\" ]\n"
			+ "            }\n"
			+ "          }\n"
			+ "        }\n"
			+ "      } ]\n"
			+ "    }\n"
			+ "  }\n"
			+ "}";

	private final CsvMapper csvMapper = new CsvMapper();

	private final InitializrMetadata metadata;

	private final JestClient jestClient;


	public DependenciesCsvExporter(InitializrMetadataProvider metadataProvider,
			JestClient jestClient) {
		this.metadata = metadataProvider.get();
		this.jestClient = jestClient;
	}

	public void writeTo(Writer writer) throws IOException {
		this.csvMapper.writerFor(new TypeReference<List<DependencyEntry>>() {})
				.with(DependencyEntry.csvSchema())
				.writeValue(writer, retrieveDependencies());
	}

	private Iterable<DependencyEntry> retrieveDependencies() {
		List<DependencyEntry> entries = new ArrayList<>();
		this.metadata.getDependencies().getContent().forEach(group -> {
			group.getContent().forEach(dependency -> entries.add(
					toDependencyEntry(group.getName(), dependency)));
		});
		return entries;
	}

	private DependencyEntry toDependencyEntry(String groupName, Dependency dependency) {
		return new DependencyEntry(dependency.getId(), dependency.getName(), groupName,
				null, dependency.getDescription(), downloadCount(dependency.getId()));
	}

	private long downloadCount(String dependencyId) {
		logger.debug("Retrieving download stats for " + dependencyId);
		Search query = new Search.Builder(indexQuery(dependencyId))
				.addIndex("initializr-archive")
				.build();
		try {
			SearchResult result = this.jestClient.execute(query);
			return result.getTotal();
		}
		catch (IOException e) {
			logger.warn("Failed to get stats", e);
		}
		return 0L;
	}

	private String indexQuery(String dependencyId) {
		return String.format(QUERY_TEMPLATE,
				LocalDate.of(2018, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
				LocalDate.of(2018, 10, 31).plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
				dependencyId);
	}

}
