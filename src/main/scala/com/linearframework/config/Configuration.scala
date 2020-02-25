package com.linearframework.config

import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Properties
import scala.util.Try
import scala.util.control.NonFatal

/**
 * Provides convenience methods for loading [[com.linearframework.config.Configuration]] objects.
 */
object Configuration {

  /**
   * Loads a [[com.linearframework.config.Configuration]] object using the path specified by `-Dconfig.file=[path]` when
   * the JVM was started.  If no JVM argument was provided, the given properties file will be used instead.
   * @param fallbackPropertiesFile the properties file to load when no `config.file` JVM argument is provided.
   *                               This file can be either on the classpath or on the external file system.
   *                               Defaults to "application.properties" on the classpath.
   */
  def load(fallbackPropertiesFile: String = "application.properties"): Configuration = {
    load("config.file", fallbackPropertiesFile)
  }

  /**
   * Loads a [[com.linearframework.config.Configuration]] object using the path specified by `-D[jvmArgument]=[path]` when
   * the JVM was started.  If no JVM argument was provided, the given properties file will be used instead.
   * @param jvmArgument            the JVM argument to check.
   * @param fallbackPropertiesFile the properties file to load when no JVM argument is provided.
   *                               This file can be either on the classpath or on the external file system.
   */
  def load(jvmArgument: String, fallbackPropertiesFile: String): Configuration = {
    val jvmPropertiesFile = Option(System.getProperty(jvmArgument))
    val propertyFile = jvmPropertiesFile.getOrElse(fallbackPropertiesFile)

    var result = loadClasspath(propertyFile)
    if (result.isEmpty) {
      result = loadExternal(propertyFile)
    }

    result
  }

  /**
   * Loads a [[com.linearframework.config.Configuration]] object using the given properties file that exists on the classpath.
   * @param propertiesFilePath the properties file to load from the classpath
   */
  def loadClasspath(propertiesFilePath: String): Configuration = {
    new ClasspathConfiguration {
      override protected val propertiesFile: String = propertiesFilePath
    }
  }

  /**
   * Loads a [[com.linearframework.config.Configuration]] object using the given properties file that exists on the external file system.
   * @param propertiesFilePath the properties file to load from the file system
   */
  def loadExternal(propertiesFilePath: String): Configuration = {
    new ExternalConfiguration {
      override protected val propertiesFile: String = propertiesFilePath
    }
  }
}

/**
 * Contains configuration defined by a properties file.
 * Properties can be overridden by environment variables, which in turn can be overridden by
 * properties created with the "-D" flag when creating the JVM
 */
trait Configuration {

  /**
   * The path to the properties file
   */
  protected val propertiesFile: String

  /**
   * The method of locating the properties file
   */
  protected def getPropertiesFile: URL

  private lazy val properties: Properties = {
    val props = new Properties()
    var reader: InputStreamReader = null
    try {
      val url = getPropertiesFile
      reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)
      props.load(reader)
    }
    catch {
      case e: ConfigurationException =>
        throw e
      case NonFatal(e) =>
        throw new ConfigurationException(s"Failed to load configuration properties [$getPropertiesFile]", e)
    }
    finally {
      Try(reader.close())
    }
    props
  }

  /**
   * Determines whether or not this object contains any properties
   */
  protected def isEmpty: Boolean = {
    try {
      properties.isEmpty
    }
    catch {
      case NonFatal(_) => true
    }
  }

  /**
   * Gets the configuration property identified by the given key.
   * If no such property exists, return the provided default value.
   * <br/><br/>
   * The property value returned by this method uses the following logic:
   * <ol>
   * <li>If a JVM property exists with the given key (i.e., the JVM was started with `"-D[key=value]")`, return that JVM property; otherwise</li>
   * <li>If an Environment variable exists with the given key, return that environment variable; otherwise</li>
   * <li>If the properties file contains the given key, return that property; otherwise</li>
   * <li>Return the default value</li>
   * </ol>
   * @param key     the property to find
   * @param default the value to return if the property was not found
   */
  final def apply(key: String, default: String): String = {
    var prop: String = System.getProperty(key)

    def notFound: Boolean = prop == null || prop.trim.isEmpty

    if (notFound) {
      prop = System.getenv(key)
    }
    if (notFound) {
      prop = Option(properties.get(key)).getOrElse("").toString
    }
    if (notFound) {
      prop = default
    }

    prop
  }

  /**
   * Calls [[com.linearframework.config.Configuration#apply(String, String)]] to fetch the configuration property identified by the given key.
   * @param key the property to find
   * @throws com.linearframework.config.PropertyNotFoundException if the given property could not be found
   * @see [[com.linearframework.config.Configuration#apply(String, String)]]
   */
  final def apply(key: String): String = {
    val prop = apply(key, null)
    if (prop == null) {
      throw new PropertyNotFoundException(s"Failed to fetch configuration property [$key]")
    }
    prop
  }

  /**
   * Gets the configuration property identified by the given key.
   * If no such property exists, return the provided default value.
   * @see [[com.linearframework.config.Configuration#apply(String, String)]]
   */
  final def getString(key: String, default: String): String = {
    apply(key, default)
  }

  /**
   * Gets the configuration property identified by the given key.
   * @param key the property to find
   * @throws com.linearframework.config.PropertyNotFoundException if the given property could not be found
   * @see [[com.linearframework.config.Configuration#apply(String)]]
   */
  final def getString(key: String): String = {
    apply(key)
  }

  /**
   * Gets the configuration property identified by the given key.
   * If no such property exists, return the provided default value.
   * @see [[com.linearframework.config.Configuration#apply(String, String)]]
   */
  final def getInt(key: String, default: Int): Int = {
    apply(key, default.toString).toInt
  }

  /**
   * Gets the configuration property identified by the given key.
   * @param key the property to find
   * @throws com.linearframework.config.PropertyNotFoundException if the given property could not be found
   * @see [[com.linearframework.config.Configuration#apply(String)]]
   */
  final def getInt(key: String): Int = {
    apply(key).toInt
  }

  /**
   * Gets the configuration property identified by the given key.
   * If no such property exists, return the provided default value.
   * @see [[com.linearframework.config.Configuration#apply(String, String)]]
   */
  final def getLong(key: String, default: Long): Long = {
    apply(key, default.toString).toLong
  }

  /**
   * Gets the configuration property identified by the given key.
   * @param key the property to find
   * @throws com.linearframework.config.PropertyNotFoundException if the given property could not be found
   * @see [[com.linearframework.config.Configuration#apply(String)]]
   */
  final def getLong(key: String): Long = {
    apply(key).toLong
  }

  /**
   * Gets the configuration property identified by the given key.
   * If no such property exists, return the provided default value.
   * @see [[com.linearframework.config.Configuration#apply(String, String)]]
   */
  final def getDouble(key: String, default: Double): Double = {
    apply(key, default.toString).toDouble
  }

  /**
   * Gets the configuration property identified by the given key.
   * @param key the property to find
   * @throws com.linearframework.config.PropertyNotFoundException if the given property could not be found
   * @see [[com.linearframework.config.Configuration#apply(String)]]
   */
  final def getDouble(key: String): Double = {
    apply(key).toDouble
  }

  /**
   * Gets the configuration property identified by the given key.
   * If no such property exists, return the provided default value.
   * @see [[com.linearframework.config.Configuration#apply(String, String)]]
   */
  final def getBoolean(key: String, default: Boolean): Boolean = {
    apply(key, default.toString).toBoolean
  }

  /**
   * Gets the configuration property identified by the given key.
   * @param key the property to find
   * @throws com.linearframework.config.PropertyNotFoundException if the given property could not be found
   * @see [[com.linearframework.config.Configuration#apply(String)]]
   */
  final def getBoolean(key: String): Boolean = {
    apply(key).toBoolean
  }

}
