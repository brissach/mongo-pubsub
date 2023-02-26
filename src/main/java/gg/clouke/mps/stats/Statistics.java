package gg.clouke.mps.stats;

import com.google.common.annotations.Beta;
import gg.acai.acava.commons.graph.Graph;

/**
 * @author Clouke
 * @since 26.02.2023 14:42
 * © mongo-pubsub - All Rights Reserved
 */
@Beta
public interface Statistics {

  static StatisticsBuilder newBuilder() {
    return new StatisticsBuilder();
  }

  Graph<Number> network();

}
