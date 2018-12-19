// @generated
/* tslint:disable */
// This file was automatically generated and should not be edited.

import { Episode } from "./../../../__generated__/globalTypes";

// ====================================================
// GraphQL query operation: Hero
// ====================================================

export interface Hero_hero_name {
  __typename: "Name";
  full: string;
}

export interface Hero_hero {
  __typename: "Human" | "Droid";
  /**
   * The timestamp this character was created
   */
  createdAt: Timestamp;
  /**
   * Which movies they appear in
   */
  appearsIn: Episode[];
  /**
   * The name of the character
   */
  name: Hero_hero_name;
}

export interface Hero {
  /**
   * Hero of the Star wars saga
   */
  hero: Hero_hero;
}
