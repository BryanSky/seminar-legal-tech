using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dashboard.Entities;

namespace Dashboard {
    public class DbManager {

        protected static string connectionString = "";

        public List<ResultTableRow> GetData(string tagRegex, string valueRegex) {
            List<ResultTableRow> results = new List<ResultTableRow>();
            string sql = "SELECT nE.Tag nE.[Value], cR.Filename " +
                         "FROM NamedEntity nE " +
                         "INNER JOIN ClassificationResultEntities cre " +
                         " on nE.ID = cre.NEID " +
                         "INNER JOIN ClassificationResult cr " +
                         "on cre.CRID = cr.ID " +
                         "WHERE nE.Tag LIKE '" + tagRegex + "' " +
                         "OR nE.[Value] LIKE '" + valueRegex + "'";
            using (SqlConnection connection = new SqlConnection(connectionString))
            using (SqlCommand command = new SqlCommand(sql, connection)) {
                connection.Open();
                using (SqlDataReader reader = command.ExecuteReader()) {
                    while (reader.Read()) {
                        ResultTableRow rtr = new ResultTableRow();

                        rtr.Klaeger = reader["klaeger"].ToString();
                        rtr.Theme = reader["theme"].ToString();
                        rtr.Target = reader["target"].ToString();
                        rtr.Tag = reader["tag"].ToString();
                        results.Add(rtr);
                    }
                }
            }
            return results;
        }
    }
}
