// // Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//  
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package bot;

/**
 * Move class
 * <p>
 * Stores a move.
 *
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

public class Move
{
    public int score;
    int mX, mY;

    public Move()
    {
        this.score = 0;
    }

    public Move(int score)
    {
        this.score = score;
    }

    public Move(int x, int y)
    {
        mX = x;
        mY = y;

        this.score = 0;
    }

    public int getX()
    {
        return mX;
    }

    public int getY()
    {
        return mY;
    }

}
