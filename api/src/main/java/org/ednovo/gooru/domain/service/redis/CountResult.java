/////////////////////////////////////////////////////////////
// CountResult.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.service.redis;

public class CountResult {
  private int count;
  private int peakCount;
  private int numberOfEmptyBuckets;
  private int latestBucketCount;

  /***
   * Constructs the count result across the buckets.
   * 
   * @param count
   *          sum of counts for all buckets
   * @param numberOfEmptyBuckets
   *          number of buckets where count is zero
   * @param peakCount
   *          max count found in the buckets
   * @param latestBucketCount
   *          the count in the latest bucket
   */
  public CountResult(int count, int numberOfEmptyBuckets, int peakCount, int latestBucketCount) {
    this.count = count;
    this.numberOfEmptyBuckets = numberOfEmptyBuckets;
    this.peakCount = peakCount;
    this.latestBucketCount = latestBucketCount;
  }

  /***
   * Gets the sum of counts accross all buckets in the tps interval window.
   * 
   * @return
   */
  public int getCount() {
    return count;
  }

  /***
   * Gets numbor of empty buckets.
   * 
   * @return
   */
  public int getNumberOfEmptyBuckets() {
    return numberOfEmptyBuckets;
  }

  /***
   * Gets the max count found in the bucket.
   * 
   * @return
   */
  public int getPeakCount() {
    return peakCount;
  }

  /***
   * Get the count in the current bucket.
   * 
   * @return
   */
  public int getLatestBucketCount() {
    return latestBucketCount;
  }
}
