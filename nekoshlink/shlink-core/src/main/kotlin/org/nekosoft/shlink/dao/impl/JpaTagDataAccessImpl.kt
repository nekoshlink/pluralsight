package org.nekosoft.shlink.dao.impl

import org.nekosoft.shlink.dao.TagDataAccess
import org.nekosoft.shlink.entity.Tag
import org.nekosoft.shlink.entity.support.TagStats
import org.nekosoft.shlink.sec.roles.IsTagAdmin
import org.nekosoft.shlink.sec.roles.IsTagEditor
import org.nekosoft.shlink.sec.roles.IsTagStatsViewer
import org.nekosoft.shlink.sec.roles.IsTagViewer
import org.nekosoft.shlink.service.exception.TagAlreadyExistsException
import org.nekosoft.shlink.service.exception.TagDoesNotExistException
import org.nekosoft.shlink.service.exception.TagListOptionsException
import org.nekosoft.shlink.vo.TagListOptions
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JpaTagDataAccessImpl(
    val repo: TagRepository,
): TagDataAccess {

    @IsTagViewer
    override fun findByName(name: String): Tag? =
        repo.findByName(name)

    @IsTagStatsViewer
    override fun findAll(options: TagListOptions?, pageable: Pageable?): Page<TagStats> {
        val actualPageable = pageable ?: Pageable.unpaged()
        val actualOptions = options ?: TagListOptions()
        if (actualOptions.id != null) {
            if (actualOptions.name != null) {
                throw TagListOptionsException()
            }
            val tag = repo.findById(actualOptions.id!!)
            return PageImpl(tag.map { listOf(tagToTagStats(it)) }.orElse(listOf()))
        } else if (actualOptions.name != null) {
            val tag = repo.findByName(actualOptions.name!!)
            return PageImpl(tag?.let { listOf(tagToTagStats(it)) } ?: listOf())
        }
        return if (actualOptions.withStats) {
            getAllTagsWithStats(actualOptions.namePattern, actualPageable)
        } else {
            getAllTags(actualOptions.namePattern, actualPageable)
        }
    }

    @IsTagEditor
    @Transactional
    override fun create(name: String, description: String?): Tag {
        if (repo.findByName(name) != null) {
            throw TagAlreadyExistsException(name)
        }
        return repo.saveAndFlush(Tag(name = name, description = description))
    }

    @IsTagAdmin
    @Transactional
    override fun deleteByName(name: String) {
        val tag = repo.findByName(name) ?: throw TagDoesNotExistException(name)
        tag.shortUrls.clear()
        repo.deleteAllShortUrlLinks(tag)
        repo.delete(tag)
    }

    @IsTagEditor
    @Transactional
    override fun rename(oldName: String, newName: String, newDescription: String?): Tag {
        val tag = repo.findByName(oldName) ?: throw TagDoesNotExistException(oldName)
        val newTag = repo.findByName(newName)
        if (newTag != null) {
            throw TagAlreadyExistsException(newName)
        }
        tag.name = newName
        if (newDescription != null) tag.description = newDescription
        return repo.saveAndFlush(tag)
    }

    @IsTagEditor
    @Transactional
    override fun describe(name: String, description: String?): Tag {
        val tag = repo.findByName(name) ?: throw TagDoesNotExistException(name)
        tag.description = description
        return repo.saveAndFlush(tag)
    }

    private fun getAllTags(containsTerm: String?, pageable: Pageable): Page<TagStats> {
        val results = if (containsTerm == null) {
            repo.findAll(pageable)
        } else {
            repo.findByNameContaining(containsTerm, pageable)
        }
        return results.map { tagToTagStats(it) }
    }

    private fun getAllTagsWithStats(containsTerm: String?, pageable: Pageable): Page<TagStats> {
        val results = if (containsTerm == null) {
            repo.tagStats(pageable)
        } else {
            repo.tagStatsByNameLike(containsTerm, pageable)
        }
        return results.map { TagStats(
            id = (it["tag"] as Tag).id ?: -1,
            name = (it["tag"] as Tag).name,
            description = (it["tag"] as Tag).description,
            shortUrlCount = it["shortUrlCount"] as Long,
            visitCount = it["visitCount"] as Long,
        ) }
    }

    private fun tagToTagStats(tag: Tag) =
        TagStats(
            id = tag.id!!,
            name = tag.name,
            description = tag.description,
            shortUrlCount = -1L,
            visitCount = -1L,
        )

}
