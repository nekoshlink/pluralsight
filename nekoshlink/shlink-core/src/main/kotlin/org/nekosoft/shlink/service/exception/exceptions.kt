package org.nekosoft.shlink.service.exception

import org.nekosoft.shlink.entity.ShortUrl.Companion.MIN_LENGTH


/**
 *
 */
sealed class NekoShlinkException(msg: String? = null) : Exception(msg)


/**
 *
 */
sealed class NekoShlinkDomainException(msg: String? = null) : NekoShlinkException(msg)

class InvalidDefaultDomainException(val domain: String) : NekoShlinkDomainException("The specified default domain is invalid : $domain")
class DefaultDomainDoesNotExistsException : NekoShlinkDomainException("A default domain does not yet exist")
class DefaultDomainAlreadyExistsException(val authority: String) : NekoShlinkDomainException("A default domain already exists : $authority")
class DomainDoesNotExistException(val authority: String) : NekoShlinkDomainException("The given domain does not exist : $authority")
class DomainAlreadyExistsException(val authority: String) : NekoShlinkDomainException("The given domain already exists : $authority")

/**
 *
 */
sealed class NekoShlinkTagException(msg: String? = null) : NekoShlinkException(msg)

class TagDoesNotExistException(val name: String) : NekoShlinkTagException("The tag does not exist : $name")
class TagAlreadyExistsException(val name: String) : NekoShlinkTagException("The tag already exists : $name")
class TagListOptionsException() : NekoShlinkTagException("You can find a Tag either by ID or by Name only")


/**
 *
 */
sealed class NekoShlinkCreationException(msg: String? = null) : NekoShlinkException(msg)

class MissingLongUrlException : NekoShlinkCreationException("A Long URL is required in order to create a Short URL")
class InvalidLongUrlException(val longUrl: String) : NekoShlinkCreationException("The given Long URL is invalid : $longUrl")
class InvalidShortCodeException(val shortCode: String) : NekoShlinkCreationException("The given Short URL is invalid : $shortCode")
class DuplicateShortCodeException(val shortCode: String, val authority: String) : NekoShlinkCreationException("The given Short URL already exists : $authority / $shortCode")
class MinimumShortCodeLengthException(val length: Int) : NekoShlinkCreationException("The length of the Short URL must be greater than $MIN_LENGTH : $length")


/**
 *
 */
sealed class NekoShlinkEditException(msg: String? = null) : NekoShlinkException(msg)

class FindOptionsException : NekoShlinkEditException("You can modify a Short URL either by ID or by Short URL + Domain")
class ShortUrlDoesNotExistException(val id: Long?, val shortCode: String?, val authority: String?) : NekoShlinkEditException("The given Short URL does not exist : id $id, $authority / $shortCode")


/**
 *
 */
sealed class NekoShlinkResolutionException(msg: String) : NekoShlinkException(msg)

class MissingShortUrlException() : NekoShlinkResolutionException("A Short URL is required in order to perform resolution")
class ProtectedShortUrlResolutionException(val shortCode: String, val authority: String) : NekoShlinkResolutionException("The Short URL resolution is protected : $authority / $shortCode")
class ShortUrlHasExpiredException(val shortCode: String, val authority: String) : NekoShlinkResolutionException("The Short URL has expired : $authority / $shortCode")
class ShortUrlNotEnabledYetException(val shortCode: String, val authority: String) : NekoShlinkResolutionException("The Short URL is not enabled yet : $authority / $shortCode")
class MaxVisitLimitReachedException(val shortCode: String, val authority: String) : NekoShlinkResolutionException("The Short URL has reached the maximum allowed number of visits : $authority / $shortCode")
class PathTrailNotAllowedException(val shortCode: String, val authority: String, val pathTrail: String) : NekoShlinkResolutionException("This Long URL cannot be resolved with a path trail : $authority / $shortCode : $pathTrail")
class QueryParamsNotAllowedException(val shortCode: String, val authority: String, val queryParams: String) : NekoShlinkResolutionException("This Long URL cannot be resolved with query parameters : $authority / $shortCode : $queryParams")
