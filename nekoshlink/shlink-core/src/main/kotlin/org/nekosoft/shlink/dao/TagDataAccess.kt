package org.nekosoft.shlink.dao

import org.nekosoft.shlink.entity.Tag
import org.nekosoft.shlink.entity.support.TagStats
import org.nekosoft.shlink.vo.TagListOptions
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TagDataAccess {

    fun findAll(options: TagListOptions? = null, pageable: Pageable? = null): Page<TagStats>
    fun create(name: String, description: String? = null): Tag
    fun findByName(name:String): Tag?
    fun deleteByName(name: String)
    fun rename(oldName: String, newName: String, newDescription: String? = null): Tag
    fun describe(name: String, description: String?): Tag

}