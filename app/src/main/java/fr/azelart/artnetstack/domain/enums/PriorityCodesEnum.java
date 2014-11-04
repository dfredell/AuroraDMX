/*
 * Copyright 2012 Corentin Azelart.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.azelart.artnetstack.domain.enums;

/**
 * Priority code (table 5).
 * @author Corentin Azelart.
 *
 */
public enum PriorityCodesEnum {

	/** Low. */
	DP_LOW,

	/** Medium. */
	DP_MEDIUM,

	/** High. */
	DP_HIGH,

	/** Critical. */
	DP_CRITICAL,

	/** Volatile (just displayed on one line). */
	DP_VOLATILE,

}
