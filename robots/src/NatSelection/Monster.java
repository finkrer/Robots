package NatSelection;

public class Monster {

    private final double SPEED = 0.1;
    private final double ROTATESPEED = 0.01;
    private final int LIFETIME = 5000;
    private double x, y, direction, targetDirection;
    private Pair<Integer, Integer> targetCoords;
    private NSMap map;
    private int foodEaten = 0;
    private double liveTimer = LIFETIME;
    private boolean isAlive = true, isGoHome = false;
    private boolean canMove = true;

    public Monster(NSMap nsMap, int x, int y)
    {
        this.x = x;
        this.y = y;
        direction = Math.random()*Math.PI*2;
        targetDirection = -1;
        map = nsMap;

        targetCoords = null;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public void update(double dt)
    {
        /*
        Чекает еду в зоне видимости
        Если есть - идет к ней
        Если нет - идет рандомно. (Точнее в +- том направлении что двигался. И если врезался - то в обратном)
         */

        if(!canMove)
            return;

        // TODO Refactoring
        double newX, newY;

        liveTimer -= dt;

        if(liveTimer < 0)
            isAlive = false;

        if(targetCoords == null)
        {
            targetCoords = map.getTarget((int)x, (int)y);
        }



        if(targetCoords != null)
        {
            targetDirection = Math.atan2(targetCoords.getSecond() - y, targetCoords.getFirst() - x);

            if(Math.abs(direction - targetDirection) < ROTATESPEED*dt || isGoHome)
            {
                direction = targetDirection;
            }
            else
            {
                if(targetDirection - direction > Math.PI)
                    direction = Math.PI*2 + direction;
                else if(targetDirection - direction < -Math.PI)
                    targetDirection = Math.PI*2 + targetDirection;


                if(targetDirection - direction > 0)
                    direction = (direction + ROTATESPEED*dt)%(Math.PI*2);
                else
                    direction = (direction - ROTATESPEED*dt)%(Math.PI*2);
            }



            if(distance(x, y, targetCoords.getFirst(), targetCoords.getSecond()) < SPEED*dt)
            {
                x = targetCoords.getFirst();
                y = targetCoords.getSecond();

                targetCoords = null;
                targetDirection = -1;

                if(isGoHome)
                {
                    canMove = false;
                    return;
                }

                map.removeFood(x, y);
                foodEaten++;

                if(foodEaten == 1)
                    liveTimer = LIFETIME;
                else if(foodEaten == 2)
                {
                    foodEaten = 0;
                    multiply();
                }
            }
            else
            {
                newX = x + SPEED * Math.cos(direction) * dt;
                newY = y + SPEED * Math.sin(direction) * dt;

                if(newX < 0 || newX > map.getWidth() || newY < 0 || newY > map.getHeight())
                    direction = (direction + Math.PI)%(Math.PI*2);
                else
                {
                    x = newX;
                    y = newY;
                }
            }
        }
        else
        {
            if(targetDirection == -1)
                targetDirection = (direction + (Math.random()*Math.PI - Math.PI/2))%(Math.PI*2);

            if(Math.abs(direction - targetDirection) < ROTATESPEED*dt)
            {
                direction = targetDirection;
                targetDirection = -1;
            }
            else
            {
                if(targetDirection - direction > 0)
                    direction = (direction + ROTATESPEED*dt)%(Math.PI*2);
                else
                    direction = (direction - ROTATESPEED*dt)%(Math.PI*2);
            }

            newX = x + SPEED * Math.cos(direction) * dt;
            newY = y + SPEED * Math.sin(direction) * dt;

            if(newX < 0 || newX > map.getWidth() || newY < 0 || newY > map.getHeight()) {
                direction = (direction + Math.PI)%(Math.PI*2);
                targetDirection = direction;
            }
            else
            {
                x = newX;
                y = newY;
            }
        }

    }

    private void multiply() {
        map.createMonster((int)x, (int)y);
    }

    public Pair<Integer, Integer> getCoords()
    {
        return new Pair<Integer, Integer>((int)(x), (int)(y));
    }

    public static double getVisionDistance() {
        return 60;
    }

    public double getDirection() {
        return direction;
    }

    public boolean isAlive()
    {
        return isAlive;
    }

    public void goHome()
    {
        isGoHome = true;

        double nX = 0, nY = 0, w = map.getWidth(), h = map.getHeight();
        double left = x, right = w-x, up = y, down = h-y;
        double min = Math.min(Math.min(left, right), Math.min(up, down));

        if(left == min)
            nY = y;
        else if(up == min)
            nX = x;
        else if(right == min)
        {
            nY = y;
            nX = w;
        }
        else
        {
            nX = x;
            nY = h;
        }

        targetCoords = new Pair<>((int)nX, (int)nY);
    }
}
