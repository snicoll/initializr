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

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

class DependencyEntry implements Comparable<DependencyEntry> {

	static CsvSchema csvSchema() {
		return CsvSchema.builder().addColumn("id").addColumn("name").addColumn("group")
				.addColumn("category").addColumn("description")
				.addColumn("count2016", CsvSchema.ColumnType.NUMBER)
				.addColumn("count2017", CsvSchema.ColumnType.NUMBER)
				.addColumn("count2018", CsvSchema.ColumnType.NUMBER).build();
	}

	private String id;

	private String name;

	private String group;

	private String category;

	private String description;

	private long count2016;

	private long count2017;

	private long count2018;

	public DependencyEntry(String id, String name, String group, String category,
			String description, long count2016, long count2017, long count2018) {
		this.id = clean(id);
		this.name = clean(name);
		this.group = clean(group);
		this.category = clean(category);
		this.description = clean(description);
		this.count2016 = count2016;
		this.count2017 = count2017;
		this.count2018 = count2018;
	}

	private static String clean(String value) {
		if (value == null) {
			return null;
		}
		return value.replace('"', ' ').trim();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCount2016() {
		return this.count2016;
	}

	public void setCount2016(long count2016) {
		this.count2016 = count2016;
	}

	public long getCount2017() {
		return this.count2017;
	}

	public void setCount2017(long count2017) {
		this.count2017 = count2017;
	}

	public long getCount2018() {
		return this.count2018;
	}

	public void setCount2018(long count2018) {
		this.count2018 = count2018;
	}

	@Override
	public int compareTo(DependencyEntry o) {
		return this.getId().compareTo(o.getId());
	}

}
