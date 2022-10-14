package org.nekosoft.shlink.dao.impl

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.nekosoft.shlink.entity.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
@JaversSpringDataAuditable
interface TagRepository : JpaRepository<Tag, Long> {

    fun findByName(name: String): Tag?

    fun findByNameContaining(namePattern: String, pageable: Pageable): Page<Tag>

    @Query("SELECT t as tag, COUNT(DISTINCT st.shortUrl.id) AS shortUrlCount, COUNT(DISTINCT v.id) AS visitCount FROM Tag t LEFT JOIN t.shortUrls st LEFT JOIN Visit v ON v.shortUrl.id = st.shortUrl.id GROUP BY t")
    fun tagStats(pageable: Pageable): Page<Map<String, Any?>>

    @Query("SELECT t as tag, COUNT(DISTINCT st.shortUrl.id) AS shortUrlCount, COUNT(DISTINCT v.id) AS visitCount FROM Tag t LEFT JOIN t.shortUrls st LEFT JOIN Visit v ON v.shortUrl.id = st.shortUrl.id WHERE t.name LIKE CONCAT('%', :namePattern, '%') GROUP BY t")
    fun tagStatsByNameLike(namePattern: String, pageable: Pageable): Page<Map<String, Any?>>

    @Modifying
    @Query("DELETE FROM ShortUrlsToTags t WHERE t.tag = ?1")
    fun deleteAllShortUrlLinks(tag: Tag)

}
