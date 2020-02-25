package com.linearframework.config

/**
 * An exception generated when a given property could not be found
 */
class PropertyNotFoundException(override val message: String) extends ConfigurationException(message) {

}
