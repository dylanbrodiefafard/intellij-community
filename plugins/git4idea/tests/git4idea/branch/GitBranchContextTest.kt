/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package git4idea.branch

import com.intellij.openapi.vcs.BranchChangeListener
import git4idea.test.GitSingleRepoTest
import junit.framework.TestCase

class GitBranchContextTest: GitSingleRepoTest() {

  fun testBranchListener() {
    var didChange = 0
    var willChange = 0
    class Listener: BranchChangeListener {
      override fun branchHasChanged(branchName: String) {
        didChange++
      }

      override fun branchWillChange(branchName: String) {
        willChange++
      }
    }

    myProject.messageBus.connect().subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, Listener())

    val worker = GitBranchWorker(myProject, myGit, GitBranchWorkerTest.TestUiHandler())
    worker.checkoutNewBranch("foo", listOf(myRepo))
    TestCase.assertEquals(1, willChange)
    TestCase.assertEquals(1, didChange)

    worker.checkout("master", false, listOf(myRepo))
    TestCase.assertEquals(2, willChange)
    TestCase.assertEquals(2, didChange)
  }
}