package com.linearframework.config

/**
 * An exception generated by the configuration module
 */
class ConfigurationException(val message: String, val cause: Throwable = null) extends RuntimeException(message, cause) {

}
