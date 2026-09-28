package com.rephone.express.model;

import java.util.List;

/** 物流轨迹（P4 实现）。 */
public record ExpressTrace(String expressNo, String companyName, List<Node> nodes) {

    public record Node(String time, String description) {
    }
}
