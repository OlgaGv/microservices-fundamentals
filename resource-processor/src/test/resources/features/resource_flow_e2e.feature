@e2e
Feature: Resource Flow End-to-End Workflow
  As a Resource Flow system user
  I want to upload MP3 files and manage song metadata
  So that I can have a complete song library management system

  Background:
    Given the resource flow system is running
    And all services are healthy and available

  Scenario: Complete song upload and metadata management workflow
    Given I have a valid MP3 file "test-song.mp3" with metadata
      | Title  | Artist     | Album        | Duration | Year |
      | Test Song | Test Artist | Test Album | 03:45   | 2023 |
    
    When I upload the MP3 file to the resource service
    Then the file should be successfully uploaded
    And I should receive a resource ID
    And the file should be stored in S3
    
    When the resource processor processes the uploaded file
    Then the MP3 file should be validated
    And metadata should be extracted from the file
    And the metadata should be sent to the song service
    And a song record should be created with the extracted metadata
    
    When I retrieve the song by its ID
    Then I should receive the complete song information
    And the song details should match the original metadata
    
    When I delete the song by its ID
    Then the song should be removed from the song service
    And the associated MP3 file should be removed from S3
    And the resource record should be deleted