/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

//
//  IntExtensions.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

val Int.isEven: Boolean get() = this % 2 == 0

val Int.isOdd: Boolean get() = !isEven
