// Oasis TOFD-5465GGH-B 4 Digit LED with PT6961 Controller
// http://gtbtech.com/?p=528
 
#include <PT6961.h>           // 引入PT6961標頭檔
 
// 腳位指定   Pin A2 = DIN
//          Pin A3 = CLK
//          Pin A4 = CS
 
PT6961 SEG7(A3, A2, A1);     // 物件實體化
boolean colon=false;            // 宣告一個冒號的變數
 
void setup()
{
    pinMode(A0, OUTPUT);  // A0設為輸出
    pinMode(A5, OUTPUT);   // A5設為輸出
    digitalWrite(A5, LOW);     // A0設為接地
    digitalWrite(A0, HIGH);    // A5設為+5V，當做電源
    SEG7.initDisplay();      // PT6961初始化
}
 
void loop()
{
    // 計數從 00:00-59:99
    int i=0;
    //SEG7.sendDigit(0xC0,17);
    //SEG7.sendDigit(0xC2,17);
    //SEG7.sendDigit(0xC4,17);

    while(true){
      i++;
      if(10==i) i=0;
      
      SEG7.sendDigits(17,17,17,i,false); // 送出數值顯示
      delay(10);         // 因為是1/100秒故延遲時間為10ms
    }
}
