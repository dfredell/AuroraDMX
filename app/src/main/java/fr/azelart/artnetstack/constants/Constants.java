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
package fr.azelart.artnetstack.constants;

/**
 * Constants file.
 * @author Corentin Azelart
 *
 */
public final class Constants {

	/**
	 * Private constructor.
	 */
	private Constants() {
		super();
	}

	/** Server port. */
	public static final short SERVER_PORT = 0x1936;

	/** IP Adress. */
	public static final String SERVER_IP = "192.168.1.17";

	/** Version 14. */
	public static final int ART_NET_VERSION = 14;

	/** General Id. */
	public static final String ID = "Art-Net";

	/** Major version. */
	public static final int VERSION_LIB_HIGHT = 1;

	/** Minor version. */
	public static final int VERSION_LIB_LOW = 0;

	/** Short Name. */
	public static final String SHORT_NAME = "ArtNetStack 0.001";

	/** Long Name. */
	public static final String LONG_NAME = "ArtNetStack 1.001 - More Informations : corentin@azelart.fr";

	/** Max Ports (4 in protocol specification). */
	public static final int MAX_PORT = 4;

	/** Size of DMX data area. */
	public static final int DMX_512_SIZE = 512;

	/** Integer escape. */
	public static final int INT_ESCAP = 0xff;

	/** Max length for short name. */
	public static final int MAX_LENGTH_SHORT_NAME = 18;

	/** Max length for long name. */
	public static final int MAX_LENGTH_LONG_NAME = 64;

	/** Max length for node report. */
	public static final int MAX_LENGTH_NODE_REPORT = 64;

	/** DMX Data value : 512. */
	public static final int DMX_DATA_LENGTH = 512;

	/** Buffer of datagramm trames. */
	public static final int SERVER_BUFFER_INPUT = 1024;
}
