// @generated
/* tslint:disable */
// This file was automatically generated and should not be edited.

import { Episode } from "./../../../__generated__/globalTypes";

// ====================================================
// GraphQL query operation: Character
// ====================================================

export interface Character_character_Human_name {
  __typename: "Name";
  full: string;
}

export interface Character_character_Human_friends_name {
  __typename: "Name";
  full: string;
}

export interface Character_character_Human_friends {
  __typename: "Human" | "Droid";
  /**
   * The id of the character
   */
  id: string;
  /**
   * The name of the character
   */
  name: Character_character_Human_friends_name;
}

export interface Character_character_Human_secondDegreeFriends_name {
  __typename: "Name";
  full: string;
}

export interface Character_character_Human_secondDegreeFriends {
  __typename: "Human" | "Droid";
  /**
   * The id of the character
   */
  id: string;
  /**
   * The name of the character
   */
  name: Character_character_Human_secondDegreeFriends_name;
}

export interface Character_character_Human {
  __typename: "Human";
  /**
   * The id of the human
   */
  id: string;
  /**
   * The moment this human was created
   */
  createdAt: Timestamp;
  /**
   * Which movies they appear in
   */
  appearsIn: Episode[];
  /**
   * The name of the human
   */
  name: Character_character_Human_name;
  /**
   * The home planet of the human, or null if unknown
   */
  homePlanet: string | null;
  /**
   * The friends of the human, or an empty list if they have none
   */
  friends: Character_character_Human_friends[];
  /**
   * The friends of the human's friends
   */
  secondDegreeFriends: Character_character_Human_secondDegreeFriends[];
}

export interface Character_character_Droid_name {
  __typename: "Name";
  full: string;
}

export interface Character_character_Droid_friends_name {
  __typename: "Name";
  full: string;
}

export interface Character_character_Droid_friends {
  __typename: "Human" | "Droid";
  /**
   * The id of the character
   */
  id: string;
  /**
   * The name of the character
   */
  name: Character_character_Droid_friends_name;
}

export interface Character_character_Droid_secondDegreeFriends_name {
  __typename: "Name";
  full: string;
}

export interface Character_character_Droid_secondDegreeFriends {
  __typename: "Human" | "Droid";
  /**
   * The id of the character
   */
  id: string;
  /**
   * The name of the character
   */
  name: Character_character_Droid_secondDegreeFriends_name;
}

export interface Character_character_Droid {
  __typename: "Droid";
  /**
   * The id of the droid
   */
  id: string;
  /**
   * The moment this droid was created
   */
  createdAt: Timestamp;
  /**
   * Which movies they appear in
   */
  appearsIn: Episode[];
  /**
   * The name of the droid
   */
  name: Character_character_Droid_name;
  /**
   * The primary function of the droid
   */
  primaryFunction: string;
  /**
   * The friends of the droid, or an empty list if they have none
   */
  friends: Character_character_Droid_friends[];
  /**
   * The friends of the droid's friends
   */
  secondDegreeFriends: Character_character_Droid_secondDegreeFriends[];
}

export type Character_character = Character_character_Human | Character_character_Droid;

export interface Character {
  /**
   * Find character by its full name
   */
  character: Character_character | null;
}

export interface CharacterVariables {
  name: string;
}
