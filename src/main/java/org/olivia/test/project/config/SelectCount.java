package org.olivia.test.project.config;

import org.olivia.test.project.config.enumerations.StatCategory;

import java.util.Map;

public record SelectCount(Map<StatCategory, Integer> count) {
}
