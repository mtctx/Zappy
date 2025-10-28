/*
 * Zappy (Zappy.annotations.main): Defaults.kt
 * Copyright (C) 2025 mtctx
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the **GNU General Public License** as published
 * by the Free Software Foundation, either **version 3** of the License, or
 * (at your option) any later version.
 *
 * This program is distributed WITHOUT ANY WARRANTY; see the
 * GNU General Public License for more details, which you should have
 * received with this program.
 *
 * SPDX-FileCopyrightText: 2025 mtctx
 * SPDX-License-Identifier: GPL-3.0-only
 */

package mtctx.zappy.annotation

const val DEFAULT_REGEX_USERNAME: String = "^[a-zA-Z0-9_]{3,16}$"
const val DEFAULT_REGEX_NUMERIC: String = "^\\d+$\n"
const val DEFAULT_REGEX_EMAIL: String = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$"
const val DEFAULT_REGEX_PASSWORD: String = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
const val DEFAULT_REGEX_TOKEN: String = "^[A-Za-z0-9-_=]{20,}$"
const val DEFAULT_REGEX_UUID: String =
    "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
const val DEFAULT_REGEX_ISO_DATE: String = "^\\d{4}-\\d{2}-\\d{2}$"
const val DEFAULT_REGEX_SLUG: String = "^[a-z0-9]+(?:-[a-z0-9]+)*$"
const val DEFAULT_REGEX_PHONE_NUMBER: String = "^\\+?[0-9]{7,15}$"
const val DEFAULT_REGEX_URL: String = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$"