### Vouchers

<ul>
   <li>The user can buy a voucher and receives a code by email.</li>
   <li>The voucher has an expiration date (e.g. 30 days after purchase).</li>
   <li>After voucher activation, the currently logged user receives the voucher sum as credit in his account.</li>
   <li>In case of a purchase, the account credit is used first.</li>
</ul><br>

#### Details

<ol>
  <li>New field in the User entity 
    <ul><li>credit - BigDecimal, @PositiveOrZero</li></ul><br>
  </li>
  
  <li>
    New entity - Voucher
    <ul>
      <li>id - UUID</li>
      <li>code - String, random 16 char length</li>
      <li>value - Integer, 10, 20 or 50</li>
      <li>validUntil - DateTime</li>
      <li>isExhausted - Boolean</li>
    </ul>
  </li><br>
  
  <li>New endpoint - POST <i>"/vouchers/{voucherCode}"</i>
    <ul>
      <li>check whether voucher is valid (expired and/or used)</li>
      <li>adds credit to the account of the current user.</li>
    </ul>
  </li><br>

  <li>Changes to OrderProcessor
    <ul>
      <li>checks whether credit is available in account on purchase processing.</li>
      <li>if credit is available, lowers the total sum of the cart as much as possible - until credit is exhausted or cart price is 0.</li>
    </ul>
  </li>
</ol>
