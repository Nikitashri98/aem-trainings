package com.trainingsite.core.models;

import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.trainingsite.core.beans.ArticleListDataBean;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Model(adaptables = Resource.class , defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL  )

public class CardModel {


    protected static final Logger logger = LoggerFactory.getLogger(CardModel.class);

    List<ArticleListDataBean> articleListDataBeanList=null;



    @Inject
    private String articleRootPath ;

    @Self
    Resource resource ;

    public String getArticleRootPath(){
        return articleRootPath ;
    }


    @PostConstruct
    protected  void init(){

        articleListDataBeanList=new ArrayList<>();



        logger.debug("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        logger.debug("ArticleListModel class Method is calling.........");

        ResourceResolver resourceResolver=resource.getResourceResolver();

        QueryBuilder builder= resourceResolver.adaptTo(QueryBuilder.class) ;
        Session session=resourceResolver.adaptTo(Session.class);

        HashMap<String,String> predicate=new HashMap<>() ;

        predicate.put("path", articleRootPath) ;
        predicate.put("type" , "cq:Page"  )  ;


        logger.debug("Predicate=====> {}  " , predicate    );

        Query query=null;
        try{
            query=builder.createQuery(PredicateGroup.create(predicate) , session  ) ;
        }catch (Exception e){
            logger.error("Error in query : "  +e );

        }


        SearchResult searchResult =query.getResult();


        for(Hit hit : searchResult.getHits()){
            String path=null ;
            try{
                path=hit.getPath();

                logger.debug("\n\n Path===>{}" , path  );

                Resource articleResource=resourceResolver.getResource(path) ;

                Page articlePage=articleResource.adaptTo(Page.class);


                String title=articlePage.getTitle();
                String description=articlePage.getDescription();

                ArticleListDataBean bean=new ArticleListDataBean();

                bean.setPath(path);
                bean.setTitle(title);
                bean.setDescription(description);

                articleListDataBeanList.add(bean);




            }catch (Exception e){
                logger.debug("Error in Accessibility--> {} " , e   );
            }
        }


        logger.debug("ArticleListDataBeanList====>{}" , articleListDataBeanList  );

    }


    public List<ArticleListDataBean> getArticleListDataBeanList() {
        return articleListDataBeanList;
    }
}