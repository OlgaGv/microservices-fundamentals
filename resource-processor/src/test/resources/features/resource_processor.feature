@component
Feature: Resource processing

  Scenario: Successfully process a valid MP3 resource
    Given a valid resource ID "123"
    When the resource processor consumes the resource ID
    Then the resource is fetched from the resource service
    And the MP3 is validated
    And metadata is extracted
    And the metadata is sent to the Song service

  Scenario: Ignore non-MP3 resource
    Given a resource ID "124" pointing to a non-MP3 file
    When the resource processor consumes the resource ID
    Then the resource is fetched from the resource service
    And the MP3 validation fails
    And no metadata is sent to the Song service