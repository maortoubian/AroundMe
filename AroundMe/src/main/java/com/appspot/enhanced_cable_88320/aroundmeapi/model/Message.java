/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-01-14 17:53:03 UTC)
 * on 2015-03-21 at 20:47:13 UTC 
 * Modify at your own risk.
 */

package com.appspot.enhanced_cable_88320.aroundmeapi.model;

/**
 * Model definition for Message.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the aroundmeapi. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Message extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String contnet;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean downloaded;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String from;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private GeoPt location;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer readRadius;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime timestamp;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String to;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getContnet() {
    return contnet;
  }

  /**
   * @param contnet contnet or {@code null} for none
   */
  public Message setContnet(java.lang.String contnet) {
    this.contnet = contnet;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getDownloaded() {
    return downloaded;
  }

  /**
   * @param downloaded downloaded or {@code null} for none
   */
  public Message setDownloaded(java.lang.Boolean downloaded) {
    this.downloaded = downloaded;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFrom() {
    return from;
  }

  /**
   * @param from from or {@code null} for none
   */
  public Message setFrom(java.lang.String from) {
    this.from = from;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Message setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public GeoPt getLocation() {
    return location;
  }

  /**
   * @param location location or {@code null} for none
   */
  public Message setLocation(GeoPt location) {
    this.location = location;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getReadRadius() {
    return readRadius;
  }

  /**
   * @param readRadius readRadius or {@code null} for none
   */
  public Message setReadRadius(java.lang.Integer readRadius) {
    this.readRadius = readRadius;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp timestamp or {@code null} for none
   */
  public Message setTimestamp(com.google.api.client.util.DateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTo() {
    return to;
  }

  /**
   * @param to to or {@code null} for none
   */
  public Message setTo(java.lang.String to) {
    this.to = to;
    return this;
  }

  @Override
  public Message set(String fieldName, Object value) {
    return (Message) super.set(fieldName, value);
  }

  @Override
  public Message clone() {
    return (Message) super.clone();
  }

}
