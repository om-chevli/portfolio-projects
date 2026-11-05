package com.omchevli.docengine.service;

/**
 * Document summarization. The shipped implementation is extractive and runs
 * fully offline ({@link ExtractiveSummaryService}). The interface is the seam
 * where an LLM-backed implementation (e.g. LangChain4j calling AWS Bedrock or
 * another provider) plugs in without changing callers — see README.
 */
public interface SummaryService {
    String summarize(String content, int maxSentences);
}
