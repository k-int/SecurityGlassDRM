class SGFilters {

  def filters = {

    all(uri:'/**') {
      before = {
        String domain = request.getHeader('Host')?.trim() ?: ''
        // def affiliate =  Affiliate.findByDomain(domain);
        // if ( affiliate ) {
        //   affiliateAccessService.setAffiliateAccess( affiliate.id as Long, session)
        // }
        return true
      }
 
      after = { model ->
        // if ( affiliateAccessService.getAffiliateAccess(session) ) {
        //   model?.affiliate = affiliateAccessService.getAffiliateAccess(session);
        // }
        return true
      }
    }
  }
}
